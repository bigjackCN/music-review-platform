package io.github.bigjackcn.musicreview.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Full stack: HTTP -> Spring Security/JWT -> service -> MyBatis -> real PostgreSQL (Docker).
 * Requires Docker to be running.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewFlowIntegrationTest {

    // One container for the whole test class; Testcontainers removes it when the JVM exits.
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:16")
            .withInitScript("schema.sql");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private JdbcTemplate jdbc;

    private final HttpClient http = HttpClient.newHttpClient();
    private long albumId;

    @BeforeEach
    void resetData() {
        jdbc.update("DELETE FROM review");
        jdbc.update("DELETE FROM app_user");
        jdbc.update("DELETE FROM album");
        albumId = jdbc.queryForObject(
                "INSERT INTO album (title, artist, release_year, genre) "
                        + "VALUES ('Test Album', 'Test Artist', 2020, 'Rock') RETURNING id",
                Long.class);
    }

    @Test
    void registerReviewRankAndDelete_fullFlow() throws Exception {
        String aliceToken = register("alice");
        String bobToken = register("bob");

        // Writing a review requires a token
        assertThat(send("POST", "/api/albums/" + albumId + "/reviews", null, "{\"rating\":5}").statusCode())
                .isIn(401, 403);

        // Out-of-range rating is a 400 from the service, not a 500
        assertThat(send("POST", "/api/albums/" + albumId + "/reviews", aliceToken, "{\"rating\":6}").statusCode())
                .isEqualTo(400);

        // alice 5 stars, bob 2 stars -> 50% positive, average 3.5
        assertThat(send("POST", "/api/albums/" + albumId + "/reviews", aliceToken,
                "{\"rating\":5,\"comment\":\"Loved it\"}").statusCode()).isEqualTo(200);
        assertThat(send("POST", "/api/albums/" + albumId + "/reviews", bobToken,
                "{\"rating\":2}").statusCode()).isEqualTo(200);

        HttpResponse<String> reviews = send("GET", "/api/albums/" + albumId + "/reviews", null, null);
        assertThat(reviews.statusCode()).isEqualTo(200);
        assertThat(reviews.body()).contains("alice", "Loved it", "bob");

        HttpResponse<String> detail = send("GET", "/api/albums/" + albumId, null, null);
        assertThat(detail.body()).contains("\"positiveRate\":50").contains("\"reviewCount\":2");

        // Same user reviewing again updates, it does not add a second review
        send("POST", "/api/albums/" + albumId + "/reviews", bobToken, "{\"rating\":4}");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM review", Integer.class)).isEqualTo(2);
        assertThat(send("GET", "/api/albums/" + albumId, null, null).body()).contains("\"positiveRate\":100");

        // The album appears in the ranking
        HttpResponse<String> ranking = send("GET", "/api/ranking?limit=5", null, null);
        assertThat(ranking.statusCode()).isEqualTo(200);
        assertThat(ranking.body()).contains("Test Album");

        // Bob cannot delete alice's review, alice can
        long aliceReviewId = jdbc.queryForObject(
                "SELECT r.id FROM review r JOIN app_user u ON u.id = r.user_id WHERE u.username = 'alice'",
                Long.class);
        assertThat(send("DELETE", "/api/reviews/" + aliceReviewId, bobToken, null).statusCode()).isEqualTo(403);
        assertThat(send("DELETE", "/api/reviews/" + aliceReviewId, aliceToken, null).statusCode()).isEqualTo(204);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM review", Integer.class)).isEqualTo(1);
    }

    @Test
    void register_duplicateUsername_isRejected_andLoginChecksPassword() throws Exception {
        register("carol");

        assertThat(send("POST", "/api/auth/register", null, registerBody("carol")).statusCode()).isEqualTo(400);

        assertThat(send("POST", "/api/auth/login", null,
                "{\"username\":\"carol\",\"password\":\"secret123\"}").statusCode()).isEqualTo(200);
        assertThat(send("POST", "/api/auth/login", null,
                "{\"username\":\"carol\",\"password\":\"wrong-password\"}").statusCode()).isEqualTo(401);
    }

    private static String registerBody(String username) {
        return "{\"username\":\"" + username + "\",\"email\":\"" + username
                + "@example.com\",\"password\":\"secret123\"}";
    }

    private String register(String username) throws Exception {
        HttpResponse<String> res = send("POST", "/api/auth/register", null, registerBody(username));
        assertThat(res.statusCode()).isEqualTo(201);
        Matcher m = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(res.body());
        assertThat(m.find()).as("token in response: " + res.body()).isTrue();
        return m.group(1);
    }

    private HttpResponse<String> send(String method, String path, String token, String body) throws Exception {
        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .method(method, body == null
                        ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(body));
        if (token != null) {
            req.header("Authorization", "Bearer " + token);
        }
        return http.send(req.build(), HttpResponse.BodyHandlers.ofString());
    }
}
