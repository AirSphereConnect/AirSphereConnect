package com.airSphereConnect.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ErrorResponseBuilder Test Suite")
class ErrorResponseBuilderTest {

    @Test
    @DisplayName("buildErrorResponse should include all required fields")
    void buildErrorResponse_shouldIncludeAllFields() {
        Map<String, Object> response = ErrorResponseBuilder.buildErrorResponse(
                HttpStatus.NOT_FOUND, "User not found", "/api/users/1", "USER_NOT_FOUND");

        assertThat(response).containsKey("timestamp");
        assertThat(response.get("status")).isEqualTo(404);
        assertThat(response.get("error")).isEqualTo("Not Found");
        assertThat(response.get("message")).isEqualTo("User not found");
        assertThat(response.get("path")).isEqualTo("/api/users/1");
        assertThat(response.get("code")).isEqualTo("USER_NOT_FOUND");
    }

    @Test
    @DisplayName("buildErrorResponse should exclude code when null")
    void buildErrorResponse_shouldExcludeNullCode() {
        Map<String, Object> response = ErrorResponseBuilder.buildErrorResponse(
                HttpStatus.BAD_REQUEST, "Invalid input", "/api/test", null);

        assertThat(response).doesNotContainKey("code");
        assertThat(response.get("status")).isEqualTo(400);
    }

    @Test
    @DisplayName("buildErrorResponse should exclude code when blank")
    void buildErrorResponse_shouldExcludeBlankCode() {
        Map<String, Object> response = ErrorResponseBuilder.buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "Error", "/api/test", "   ");

        assertThat(response).doesNotContainKey("code");
        assertThat(response.get("status")).isEqualTo(500);
    }

    @Test
    @DisplayName("buildValidationErrorResponse should include field errors")
    void buildValidationErrorResponse_shouldIncludeFieldErrors() {
        Map<String, String> fieldErrors = Map.of(
                "email", "Invalid email format",
                "password", "Password too short"
        );

        Map<String, Object> response = ErrorResponseBuilder.buildValidationErrorResponse(
                fieldErrors, "/api/users/signup");

        assertThat(response).containsKey("timestamp");
        assertThat(response.get("status")).isEqualTo(400);
        assertThat(response.get("error")).isEqualTo("Validation Failed");
        assertThat(response.get("message")).isEqualTo("Certains champs sont invalides");
        assertThat(response.get("path")).isEqualTo("/api/users/signup");
        assertThat(response.get("fieldErrors")).isEqualTo(fieldErrors);
    }

    @Test
    @DisplayName("buildValidationErrorResponse with empty field errors")
    void buildValidationErrorResponse_withEmptyFieldErrors() {
        Map<String, String> fieldErrors = Map.of();

        Map<String, Object> response = ErrorResponseBuilder.buildValidationErrorResponse(
                fieldErrors, "/api/test");

        assertThat(response.get("fieldErrors")).isEqualTo(fieldErrors);
        assertThat(response.get("status")).isEqualTo(400);
    }
}
