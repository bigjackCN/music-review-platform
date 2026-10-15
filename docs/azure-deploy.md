# Deploying to Azure

Architecture:

```
Browser ──HTTPS──> Container App "frontend" (nginx, public)
                         │  /api/*  (same origin, proxied)
                         ▼
                   Container App "backend" (Spring Boot, internal ingress only)
                         │  SSL
                         ▼
                   Azure Database for PostgreSQL (Flexible Server)
```

Images live in Azure Container Registry. All commands are PowerShell, run from the repo root with Docker Desktop
running. Commands marked **(once)** only need to be run the first time. This guide is the sequence that actually worked
on an *Azure for Students* subscription; the notes explain the problems that came up on the way.

## 1. Variables and secrets (once)

Registry and database names must be globally unique, hence the random suffix. **Save the secrets file: if you lose
the database password you have to reset it.** It lives outside the repo on purpose.

```powershell
$RG    = "rotten-potatoes-rg"
$LOC   = "centralus"          # see step 2: students can only deploy to a few regions
$SUF   = -join ((97..122) | Get-Random -Count 5 | ForEach-Object { [char]$_ })
$ACR   = "rottenpotatoes$SUF"
$PG    = "rotten-potatoes-db-$SUF"
$CAENV = "rotten-potatoes-env"
$DB_ADMIN = "rpadmin"
$DB_PASS  = -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 24 | ForEach-Object { [char]$_ })
$JWT      = -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 48 | ForEach-Object { [char]$_ })

@"
RG=$RG
LOC=$LOC
ACR=$ACR
PG=$PG
CAENV=$CAENV
DB_ADMIN=$DB_ADMIN
DB_PASS=$DB_PASS
JWT=$JWT
"@ | Set-Content "$HOME\rotten-potatoes-secrets.txt"
```

If you open a new terminal, re-create the variables from that file. If `az` is "not recognized" in a terminal that was
opened before Azure CLI was installed, refresh the path:
`$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")`

## 2. Region, providers and resource group (once)

Student subscriptions have a policy that only allows a few regions. List them:

```powershell
az policy assignment list --query "[].{name:displayName, locations:parameters.listOfAllowedLocations.value}" -o json
```

In our case the allowed regions were `northcentralus, centralus, southcentralus, mexicocentral, eastus2`.
Creating PostgreSQL in `canadacentral` failed with `RequestDisallowedByAzure`, and in `eastus2` with
"The location is restricted from performing this operation". **`centralus` worked.** If it fails for you, try the others.

```powershell
az provider register -n Microsoft.App --wait
az provider register -n Microsoft.OperationalInsights --wait
az provider register -n Microsoft.DBforPostgreSQL --wait
az provider register -n Microsoft.ContainerRegistry --wait

az group create -n $RG -l $LOC
```

## 3. Database (once)

```powershell
az postgres flexible-server create -g $RG -n $PG -l $LOC `
  --admin-user $DB_ADMIN --admin-password $DB_PASS `
  --tier Burstable --sku-name Standard_B1ms --storage-size 32 --version 16 `
  --public-access 0.0.0.0 --yes

az postgres flexible-server db create -g $RG -s $PG -n music_review
```

The command prints a JSON block that **contains the admin password**: do not paste it anywhere. (`--database-name` is no
longer accepted by `create`; the database is created separately.) `--public-access 0.0.0.0` lets Azure services, i.e. the
Container Apps, connect.

Load the schema and demo data from your machine. Temporarily allow your IP; psql runs in a Docker container so nothing
needs installing:

```powershell
$MYIP = (Invoke-RestMethod https://api.ipify.org)
az postgres flexible-server firewall-rule create -g $RG -s $PG -n my-ip --start-ip-address $MYIP --end-ip-address $MYIP

foreach ($f in "schema","seed","seed-itunes","seed-albums-extra","seed-users","seed-reviews") {
  "== $f"
  docker run --rm -v "${PWD}\db:/db" -e PGPASSWORD=$DB_PASS postgres:16 `
    psql "host=$PG.postgres.database.azure.com port=5432 dbname=music_review user=$DB_ADMIN sslmode=require" `
    -v ON_ERROR_STOP=1 -f "/db/$f.sql"
}

az postgres flexible-server firewall-rule delete -g $RG -s $PG -n my-ip --yes
```

Expected row counts: 18 users inserted by `seed-users`, 85 reviews by `seed-reviews`.

## 4. Container registry and images

```powershell
az acr create -g $RG -n $ACR --sku Basic --admin-enabled true -l $LOC
az acr login -n $ACR

$TAG = "v1"
docker build -t "$ACR.azurecr.io/backend:$TAG"  ./backend
docker build -t "$ACR.azurecr.io/frontend:$TAG" ./frontend
docker push "$ACR.azurecr.io/backend:$TAG"
docker push "$ACR.azurecr.io/frontend:$TAG"
```

## 5. Container Apps environment (once)

```powershell
az containerapp env create -g $RG -n $CAENV -l $LOC
```

This also creates a Log Analytics workspace (needed to read logs, see step 8). On a student subscription this
environment turned out to be an **Express environment**, which behaves a bit differently (see the notes below).

## 6. Backend (internal ingress only)

```powershell
$ACR_USER = az acr credential show -n $ACR --query username -o tsv
$ACR_PASS = az acr credential show -n $ACR --query "passwords[0].value" -o tsv

az containerapp create -g $RG -n backend --environment $CAENV `
  --image "$ACR.azurecr.io/backend:$TAG" `
  --registry-server "$ACR.azurecr.io" --registry-username $ACR_USER --registry-password $ACR_PASS `
  --target-port 8080 --ingress internal `
  --cpu 0.5 --memory 1.0Gi --min-replicas 1 --max-replicas 1 `
  --secrets "db-pass=$DB_PASS" "jwt=$JWT" `
  --env-vars "DB_URL=jdbc:postgresql://$PG.postgres.database.azure.com:5432/music_review?sslmode=require" `
             "DB_USERNAME=$DB_ADMIN" "DB_PASSWORD=secretref:db-pass" "JWT_SECRET=secretref:jwt"

az containerapp revision list -g $RG -n backend --query "[].{name:name, health:properties.healthState, running:properties.runningState}" -o table
```

Database credentials and the JWT secret are stored as Container App secrets, not in the image or the repo.
The revision should be `Healthy` / `Running`.

## 7. Frontend (public)

nginx must be given the backend's **full address**. The short name `http://backend` did not resolve in this environment
(nginx exits with `host not found in upstream "backend"`).

```powershell
$BACK = az containerapp show -g $RG -n backend --query properties.configuration.ingress.fqdn -o tsv

az containerapp create -g $RG -n frontend --environment $CAENV `
  --image "$ACR.azurecr.io/frontend:$TAG" `
  --registry-server "$ACR.azurecr.io" --registry-username $ACR_USER --registry-password $ACR_PASS `
  --target-port 80 --ingress external `
  --cpu 0.25 --memory 0.5Gi --min-replicas 0 --max-replicas 2 `
  --env-vars "BACKEND_URL=https://$BACK"

az containerapp show -g $RG -n frontend --query properties.configuration.ingress.fqdn -o tsv
```

The last command prints the public address. Test the whole chain (frontend, backend, database):

```powershell
Invoke-RestMethod "https://<frontend address>/api/ranking?limit=3" | ConvertTo-Json -Depth 3
```

## 8. Logs

`az containerapp logs show` crashes on Express environments (`KeyError: 'eventStreamEndpoint'`). Query the Log Analytics
workspace instead (new log lines can take a few minutes to appear; confirm the `-w` value with the first command):

```powershell
$WS = az containerapp env show -g $RG -n $CAENV --query properties.appLogsConfiguration.logAnalyticsConfiguration.customerId -o tsv
az monitor log-analytics query -w $WS --analytics-query "ContainerAppConsoleLogs_CL | where ContainerAppName_s == 'backend' | order by TimeGenerated desc | take 20 | project TimeGenerated, Log_s" -o table
```

## 9. Releasing a new version

```powershell
$TAG = "v2"
docker build -t "$ACR.azurecr.io/frontend:$TAG" ./frontend
docker push "$ACR.azurecr.io/frontend:$TAG"
az containerapp update -g $RG -n frontend --image "$ACR.azurecr.io/frontend:$TAG"
```

(Same for the backend.) Open the site with Ctrl+F5 afterwards to avoid a cached copy.

## Troubleshooting notes (Express environment)

- **A running app did not pick up changed environment variables.** `az containerapp update --set-env-vars` reported the new
  value, but the container kept the old one and kept crashing. `revision restart` returned `InternalServerError` and
  `--revision-suffix` is not supported. What worked: delete the app (`az containerapp delete -g $RG -n frontend --yes`)
  and create it again with the right `--env-vars`. Updating the **image** with `az containerapp update --image` works normally.
- **"Invalid CORS request" on login.** The browser sends an `Origin` header on POST requests; the backend only allows
  `http://localhost:5173`. Because the site is served from one origin, nginx removes the header when it proxies
  `/api/` (`proxy_set_header Origin "";` in `frontend/nginx.conf.template`), so no CORS configuration is needed in production.
- Command argument differences that tripped us: `db create` takes the database as `-n`, and `firewall-rule create/delete`
  take the server as `-s` and the rule name as `-n`.

## Cost and cleanup

- The backend keeps one replica running so the first visitor does not wait for Spring Boot to start; the frontend scales
  to zero when idle (the first request after idle takes a few seconds). The CLI labels the database SKU "Paid Tier";
  check Cost Management in the portal after a day to see the real spend against the student credit.
- To remove everything: `az group delete -n $RG --yes --no-wait`.

## Known limitations

- The app connects to the database as the server admin. A dedicated least-privilege database user would be better.
- The database accepts connections from any Azure service; a private network (VNet) would tighten this.
- No custom domain; the default `*.azurecontainerapps.io` address is used.
