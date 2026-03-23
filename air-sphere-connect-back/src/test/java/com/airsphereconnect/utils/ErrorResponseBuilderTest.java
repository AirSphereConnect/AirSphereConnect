package com.airsphereconnect.utils;

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

        assertThat(response)
                .containsKey("timestamp")
                .containsEntry("status", 404)
                .containsEntry("error", "Not Found")
                .containsEntry("message", "User not found")
                .containsEntry("path", "/api/users/1")
                .containsEntry("code", "USER_NOT_FOUND");
    }

    @Test
    @DisplayName("buildErrorResponse should exclude code when null")
    void buildErrorResponse_shouldExcludeNullCode() {
        Map<String, Object> response = ErrorResponseBuilder.buildErrorResponse(
                HttpStatus.BAD_REQUEST, "Invalid input", "/api/test", null);

        assertThat(response)
                .doesNotContainKey("code")
                .containsEntry("status", 400);
    }

    @Test
    @DisplayName("buildErrorResponse should exclude code when blank")
    void buildErrorResponse_shouldExcludeBlankCode() {
        Map<String, Object> response = ErrorResponseBuilder.buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "Error", "/api/test", "   ");

        assertThat(response)
                .doesNotContainKey("code")
                .containsEntry("status", 500);
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

        assertThat(response)
                .containsKey("timestamp")
                .containsEntry("status", 400)
                .containsEntry("error", "Validation Failed")
                .containsEntry("message", "Certains champs sont invalides")
                .containsEntry("path", "/api/users/signup")
                .containsEntry("fieldErrors", fieldErrors);
    }

    @Test
    @DisplayName("buildValidationErrorResponse with empty field errors")
    void buildValidationErrorResponse_withEmptyFieldErrors() {
        Map<String, String> fieldErrors = Map.of();

        Map<String, Object> response = ErrorResponseBuilder.buildValidationErrorResponse(
                fieldErrors, "/api/test");

        assertThat(response)
                .containsEntry("fieldErrors", fieldErrors)
                .containsEntry("status", 400);
    }
}
