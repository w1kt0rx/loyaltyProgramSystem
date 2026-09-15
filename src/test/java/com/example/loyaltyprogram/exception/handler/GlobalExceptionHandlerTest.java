package com.example.loyaltyprogram.exception.handler;

import com.example.loyaltyprogram.dto.response.ErrorResponse;
import com.example.loyaltyprogram.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void userNotFound_mapsTo404() {
        // Given
        UserNotFoundException exception = new UserNotFoundException(1L);
        // When
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().errorCode()).isEqualTo("USER_NOT_FOUND");
    }

    @Test
    void membershipAlreadyExists_mapsTo409() {
        // Given
        MembershipAlreadyExistsException exception = new MembershipAlreadyExistsException(1L, 2L);
        // When
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void insufficientPoints_mapsTo409() {
        // Given
        InsufficientPointsException exception = new InsufficientPointsException(1L, 100, 10);
        // When
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void programExpired_mapsTo409() {
        // Given
        ProgramExpiredException exception = new ProgramExpiredException(1L);
        // When
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void forbiddenOperation_mapsTo403() {
        // Given
        ForbiddenOperationException exception = new ForbiddenOperationException("not your program");
        // When
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void illegalDateException_mapsTo400_notConflict() {
        // Given
        IllegalDateException exception = new IllegalDateException("endDate before startDate");
        // When
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void invalidRequestException_mapsTo400_notConflict() {
        // Given
        InvalidRequestException exception = new InvalidRequestException("email must not be blank");
        // When
        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(exception);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void dataIntegrityViolation_mapsTo409_andDoesNotLeakConstraintDetails() {
        // Given
        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("uk_membership_user_program constraint [23505-42]");
        // When
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(exception);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).doesNotContain("uk_membership_user_program");
    }

    @Test
    void unexpectedException_mapsTo500_withGenericMessage_noStackTraceOrClassName() {
        // Given
        NullPointerException exception = new NullPointerException("very secret internal detail");
        // When
        ResponseEntity<ErrorResponse> response = handler.handleUnexpected(exception);
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().message()).doesNotContain("NullPointerException");
        assertThat(response.getBody().message()).doesNotContain("very secret internal detail");
    }
}