package com.airsphereconnect.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("GlobalException Test Suite")
class GlobalExceptionTest {

    @Nested
    @DisplayName("ResourceNotFoundException tests")
    class ResourceNotFoundExceptionTests {

        @Test
        @DisplayName("should create exception with message")
        void shouldCreateWithMessage() {
            GlobalException.ResourceNotFoundException ex =
                    new GlobalException.ResourceNotFoundException("User not found");

            assertThat(ex.getMessage()).isEqualTo("User not found");
            assertThat(ex).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should be throwable")
        void shouldBeThrowable() {
            assertThatThrownBy(() -> {
                throw new GlobalException.ResourceNotFoundException("Not found");
            }).isInstanceOf(GlobalException.ResourceNotFoundException.class)
                    .hasMessage("Not found");
        }
    }

    @Nested
    @DisplayName("BadRequestException tests")
    class BadRequestExceptionTests {

        @Test
        @DisplayName("should create exception with message")
        void shouldCreateWithMessage() {
            GlobalException.BadRequestException ex =
                    new GlobalException.BadRequestException("Invalid input");

            assertThat(ex.getMessage()).isEqualTo("Invalid input");
            assertThat(ex).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should be throwable")
        void shouldBeThrowable() {
            assertThatThrownBy(() -> {
                throw new GlobalException.BadRequestException("Bad request");
            }).isInstanceOf(GlobalException.BadRequestException.class)
                    .hasMessage("Bad request");
        }
    }

    @Nested
    @DisplayName("UnauthorizedException tests")
    class UnauthorizedExceptionTests {

        @Test
        @DisplayName("should create exception with message")
        void shouldCreateWithMessage() {
            GlobalException.UnauthorizedException ex =
                    new GlobalException.UnauthorizedException("Not authenticated");

            assertThat(ex.getMessage()).isEqualTo("Not authenticated");
            assertThat(ex).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should be throwable")
        void shouldBeThrowable() {
            assertThatThrownBy(() -> {
                throw new GlobalException.UnauthorizedException("Unauthorized");
            }).isInstanceOf(GlobalException.UnauthorizedException.class)
                    .hasMessage("Unauthorized");
        }
    }

    @Nested
    @DisplayName("ForbiddenException tests")
    class ForbiddenExceptionTests {

        @Test
        @DisplayName("should create exception with message")
        void shouldCreateWithMessage() {
            GlobalException.ForbiddenException ex =
                    new GlobalException.ForbiddenException("Access denied");

            assertThat(ex.getMessage()).isEqualTo("Access denied");
            assertThat(ex).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should be throwable")
        void shouldBeThrowable() {
            assertThatThrownBy(() -> {
                throw new GlobalException.ForbiddenException("Forbidden");
            }).isInstanceOf(GlobalException.ForbiddenException.class)
                    .hasMessage("Forbidden");
        }
    }

    @Nested
    @DisplayName("JwtAuthenticationException tests")
    class JwtAuthenticationExceptionTests {

        @Test
        @DisplayName("should create exception with message")
        void shouldCreateWithMessage() {
            GlobalException.JwtAuthenticationException ex =
                    new GlobalException.JwtAuthenticationException("Token expired");

            assertThat(ex.getMessage()).isEqualTo("Token expired");
            assertThat(ex).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should be throwable")
        void shouldBeThrowable() {
            assertThatThrownBy(() -> {
                throw new GlobalException.JwtAuthenticationException("Invalid token");
            }).isInstanceOf(GlobalException.JwtAuthenticationException.class)
                    .hasMessage("Invalid token");
        }
    }
}
