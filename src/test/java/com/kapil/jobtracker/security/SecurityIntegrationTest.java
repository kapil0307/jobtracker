package com.kapil.jobtracker.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;

import java.util.Date;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${app.jwt.secret}")
    private String jwtSecret;


    @Test
    void shouldReturn401WhenAccessTokenIsMissing() throws Exception{
        mockMvc.perform(get("/api/companies"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication is required"))
                .andExpect(jsonPath("$.path").value("/api/companies"));
    }

    @Test
    void shouldReturn403WhenUserAccessAdminEndpoint() throws Exception{
        mockMvc.perform(
                get("/api/users")
                        .with(user("user@example.com").roles("USER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"))
                .andExpect(jsonPath("$.path").value("/api/users"));

    }

    @Test
    void shouldAllowAdminToAccessAdminEndpoint() throws Exception{
        mockMvc.perform(
                get("/api/users")
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WhenAccessTokenIsInvalid() throws Exception{
        mockMvc.perform(get("/api/companies")
                .header("Authorization", "Bearer invalid-token")
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid or expired token"));
    }


    //Method for creating expired token
    //issuedAt   → 2 minute pehle
    //expiration → 1 minute pehle
    private String createExpiredToken() {

        SecretKey key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );

        return Jwts.builder()
                .subject("user@example.com")
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(key)
                .compact();
    }

    @Test
    void shouldReturn401WhenAccessTokenIsExpired() throws Exception{
        String expiredToken = createExpiredToken();

        mockMvc.perform(
                get("/api/companies")
                        .header("Authorization", "Bearer " + expiredToken)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid or expired token"));
    }


}
