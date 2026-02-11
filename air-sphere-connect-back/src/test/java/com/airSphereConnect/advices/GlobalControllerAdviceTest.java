package com.airSphereConnect.advices;

import com.airSphereConnect.exceptions.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalControllerAdvice Test Suite")
class GlobalControllerAdviceTest {

    private GlobalControllerAdvice advice;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        advice = new GlobalControllerAdvice();
        request = new MockHttpServletRequest("GET", "/api/test");
    }

    @Test
    @DisplayName("handleAllExceptions should return 500 with ProblemDetail")
    void handleAllExceptions_shouldReturn500() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<ProblemDetail> response = advice.handleAllExceptions(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Erreur interne");
        assertThat(response.getBody().getDetail()).isEqualTo("Une erreur interne est survenue");
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/api/test");
    }

    @Test
    @DisplayName("handleNotFound should return 404 with ProblemDetail")
    void handleNotFound_shouldReturn404() {
        GlobalException.ResourceNotFoundException ex = new GlobalException.ResourceNotFoundException("User not found");

        ResponseEntity<ProblemDetail> response = advice.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Ressource non trouvée");
        assertThat(response.getBody().getDetail()).isEqualTo("User not found");
    }

    @Test
    @DisplayName("handleBadRequestException should return 400 with ProblemDetail")
    void handleBadRequestException_shouldReturn400() {
        GlobalException.BadRequestException ex = new GlobalException.BadRequestException("Invalid input");

        ResponseEntity<ProblemDetail> response = advice.handleBadRequestException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Requête invalide");
        assertThat(response.getBody().getDetail()).isEqualTo("Invalid input");
    }

    @Test
    @DisplayName("handleUnauthorizedException should return 401 with ProblemDetail")
    void handleUnauthorizedException_shouldReturn401() {
        GlobalException.UnauthorizedException ex = new GlobalException.UnauthorizedException("Not authenticated");

        ResponseEntity<ProblemDetail> response = advice.handleUnauthorizedException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Authentification requise");
        assertThat(response.getBody().getDetail()).isEqualTo("Not authenticated");
    }

    @Test
    @DisplayName("handleForbiddenException should return 403 with ProblemDetail")
    void handleForbiddenException_shouldReturn403() {
        GlobalException.ForbiddenException ex = new GlobalException.ForbiddenException("Access denied");

        ResponseEntity<ProblemDetail> response = advice.handleForbiddenException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Accès refusé");
        assertThat(response.getBody().getDetail()).isEqualTo("Access denied");
    }
}
