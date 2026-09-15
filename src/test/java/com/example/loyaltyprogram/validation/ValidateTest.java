package com.example.loyaltyprogram.validation;

import com.example.loyaltyprogram.exception.InvalidRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

public class ValidateTest {

    @Test
    void privateConstructor_throwsExceptionOrIsAccessible() throws Exception {
        // given
        Constructor<Validate> constructor = Validate.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        // when
        Validate instance = constructor.newInstance();
        // then
        Assertions.assertNotNull(instance);
    }

    @Test
    void notBlank_validValue_doesNotThrowException() {
        // given
        String value = "valid string";
        // when + then
        Assertions.assertDoesNotThrow(() -> Validate.notBlank(value, "field"));
    }

    @Test
    void notBlank_nullValue_throwsInvalidRequestException() {
        // given
        String value = null;
        // when + then
        InvalidRequestException ex = Assertions.assertThrows(
                InvalidRequestException.class,
                () -> Validate.notBlank(value, "username")
        );
        Assertions.assertEquals("username cannot be blank", ex.getMessage());
    }

    @Test
    void notBlank_blankValue_throwsInvalidRequestException() {
        // given
        String value = "   ";
        // when + then
        InvalidRequestException ex = Assertions.assertThrows(
                InvalidRequestException.class,
                () -> Validate.notBlank(value, "username")
        );
        Assertions.assertEquals("username cannot be blank", ex.getMessage());
    }

    @Test
    void notNull_validObject_doesNotThrowException() {
        // given
        Object value = new Object();
        // when + then
        Assertions.assertDoesNotThrow(() -> Validate.notNull(value, "field"));
    }

    @Test
    void notNull_nullObject_throwsInvalidRequestException() {
        // given
        Object value = null;
        // when + then
        InvalidRequestException ex = Assertions.assertThrows(
                InvalidRequestException.class,
                () -> Validate.notNull(value, "programId")
        );
        Assertions.assertEquals("programId is required", ex.getMessage());
    }

    @Test
    void positive_positiveValue_doesNotThrowException() {
        // given
        int value = 10;
        // when + then
        Assertions.assertDoesNotThrow(() -> Validate.positive(value, "points"));
    }

    @Test
    void positive_zeroValue_throwsInvalidRequestException() {
        // given
        int value = 0;
        // when + then
        InvalidRequestException ex = Assertions.assertThrows(
                InvalidRequestException.class,
                () -> Validate.positive(value, "points")
        );
        Assertions.assertEquals("points must be greater than 0", ex.getMessage());
    }

    @Test
    void positive_negativeValue_throwsInvalidRequestException() {
        // given
        int value = -5;
        // when + then
        InvalidRequestException ex = Assertions.assertThrows(
                InvalidRequestException.class,
                () -> Validate.positive(value, "points")
        );
        Assertions.assertEquals("points must be greater than 0", ex.getMessage());
    }

    @Test
    void nonNegative_nullValue_doesNotThrowException() {
        // given
        Integer value = null;
        // when + then
        Assertions.assertDoesNotThrow(() -> Validate.nonNegative(value, "quantity"));
    }

    @Test
    void nonNegative_positiveValue_doesNotThrowException() {
        // given
        Integer value = 5;
        // when + then
        Assertions.assertDoesNotThrow(() -> Validate.nonNegative(value, "quantity"));
    }

    @Test
    void nonNegative_zeroValue_doesNotThrowException() {
        // given
        Integer value = 0;
        // when + then
        Assertions.assertDoesNotThrow(() -> Validate.nonNegative(value, "quantity"));
    }

    @Test
    void nonNegative_negativeValue_throwsInvalidRequestException() {
        // given
        Integer value = -1;
        // when + then
        InvalidRequestException ex = Assertions.assertThrows(
                InvalidRequestException.class,
                () -> Validate.nonNegative(value, "quantity")
        );
        Assertions.assertEquals("quantity cannot be negative", ex.getMessage());
    }

    @Test
    void email_validEmail_doesNotThrowException() {
        // given
        String email = "test@example.com";
        // when + then
        Assertions.assertDoesNotThrow(() -> Validate.email(email));
    }

    @Test
    void email_nullEmail_throwsInvalidRequestException() {
        // given
        String email = null;
        // when + then
        InvalidRequestException ex = Assertions.assertThrows(
                InvalidRequestException.class,
                () -> Validate.email(email)
        );
        Assertions.assertEquals("email cannot be blank", ex.getMessage());
    }

    @Test
    void email_invalidFormatEmail_throwsInvalidRequestException() {
        // given
        String email = "invalid-email-format";
        // when + then
        InvalidRequestException ex = Assertions.assertThrows(
                InvalidRequestException.class,
                () -> Validate.email(email)
        );
        Assertions.assertEquals("email has an invalid format", ex.getMessage());
    }
}