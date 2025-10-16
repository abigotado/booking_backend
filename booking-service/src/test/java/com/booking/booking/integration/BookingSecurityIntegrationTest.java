package com.booking.booking.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.booking.booking.BookingServiceApplication;
import com.booking.booking.service.dto.AuthRequest;
import com.booking.booking.service.dto.AuthResponse;
import com.booking.booking.service.dto.BookingRequest;
import com.booking.booking.service.dto.UserRegistrationRequest;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = BookingServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BookingSecurityIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void getBookings_withoutToken_returnsUnauthorized() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/api/bookings", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void adminEndpoints_withUserToken_returnsForbidden() {
        register("user", "UserPass123");
        String token = authenticate("user", "UserPass123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/api/users", HttpMethod.GET, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private void register(String username, String password) {
        UserRegistrationRequest request = new UserRegistrationRequest(username, password);
        restTemplate.postForEntity(baseUrl + "/api/users/register", request, AuthResponse.class);
    }

    private String authenticate(String username, String password) {
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(baseUrl + "/api/users/auth",
            new AuthRequest(username, password), AuthResponse.class);
        return response.getBody().token();
    }
}
