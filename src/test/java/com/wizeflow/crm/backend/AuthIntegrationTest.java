package com.wizeflow.crm.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizeflow.crm.backend.controller.dto.LoginRequest;
import com.wizeflow.crm.backend.controller.dto.RefreshTokenRequest;
import com.wizeflow.crm.backend.controller.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void anonymousCannotCreateCompany() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"name\":\"Acme\",\"slug\":\"acme\",\"phone\":\"12345678901\"}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity(url("/companies"), entity, String.class);

        assertThat(resp.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }

    @Test
    void userCannotCreateCompanyAndRefreshFlow() throws Exception {
        // Register
        RegisterRequest reg = RegisterRequest.builder()
                .name("Test User")
                .email("test.user@example.com")
                .password("password123")
                .phone("12345678901")
                .build();

        ResponseEntity<String> regResp = restTemplate.postForEntity(url("/api/auth/register"), reg, String.class);
        assertThat(regResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Login
        LoginRequest login = new LoginRequest();
        login.setEmail("test.user@example.com");
        login.setPassword("password123");

        ResponseEntity<String> loginResp = restTemplate.postForEntity(url("/api/auth/login"), login, String.class);
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        JsonNode loginJson = mapper.readTree(loginResp.getBody());
        String accessToken = loginJson.get("accessToken").asText();
        String refreshToken = loginJson.get("refreshToken").asText();

        // Try to create company with user token -> should be forbidden
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        String body = "{\"name\":\"Acme\",\"slug\":\"acme2\",\"phone\":\"12345678901\"}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> createResp = restTemplate.postForEntity(url("/companies"), entity, String.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // Refresh with refresh token -> should succeed
        RefreshTokenRequest refreshReq = new RefreshTokenRequest();
        refreshReq.setRefreshToken(refreshToken);

        ResponseEntity<String> refreshResp = restTemplate.postForEntity(url("/api/auth/refresh"), refreshReq, String.class);
        assertThat(refreshResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Try to use access token as refresh -> should fail
        RefreshTokenRequest badRefresh = new RefreshTokenRequest();
        badRefresh.setRefreshToken(accessToken);
        ResponseEntity<String> badResp = restTemplate.postForEntity(url("/api/auth/refresh"), badRefresh, String.class);
        assertThat(badResp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
