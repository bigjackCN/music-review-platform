package io.github.bigjackcn.musicreview.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String SECRET = "unit-test-secret-key-that-is-at-least-32-bytes-long";
    private final JwtUtil jwtUtil = new JwtUtil(SECRET);

    @Test
    void generatedToken_isValidAndCarriesUsername() {
        String token = jwtUtil.generateToken("alice");

        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("alice");
    }

    @Test
    void tokenSignedWithAnotherSecret_isInvalid() {
        JwtUtil other = new JwtUtil("a-completely-different-secret-key-also-32-bytes-long");

        String forged = other.generateToken("alice");

        assertThat(jwtUtil.isTokenValid(forged)).isFalse();
    }

    @Test
    void expiredToken_isInvalid() {
        String expired = Jwts.builder()
                .subject("alice")
                .issuedAt(new Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000))
                .expiration(new Date(System.currentTimeMillis() - 60 * 60 * 1000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();

        assertThat(jwtUtil.isTokenValid(expired)).isFalse();
    }

    @Test
    void malformedToken_isInvalid() {
        assertThat(jwtUtil.isTokenValid("not-a-jwt")).isFalse();
        assertThat(jwtUtil.isTokenValid("")).isFalse();
    }
}
