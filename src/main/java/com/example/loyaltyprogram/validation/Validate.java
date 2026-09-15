package com.example.loyaltyprogram.validation;

import com.example.loyaltyprogram.exception.FieldValidationException;
import com.example.loyaltyprogram.exception.InvalidRequestException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validate {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public static void notBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestException(fieldName + " cannot be blank");
        }
    }

    public static void notNull(Object value, String fieldName) {
        if (value == null) {
            throw new InvalidRequestException(fieldName + " is required");
        }
    }

    public static void positive(int value, String fieldName) {
        if (value <= 0) {
            throw new InvalidRequestException(fieldName + " must be greater than 0");
        }
    }

    public static void nonNegative(Integer value, String fieldName) {
        if (value != null && value < 0) {
            throw new InvalidRequestException(fieldName + " cannot be negative");
        }
    }

    public static void email(String value) {
        notBlank(value, "email");
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidRequestException("email has an invalid format");
        }
    }

    public static void date(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate != null && !endDate.isAfter(startDate)) {
            throw new FieldValidationException("endDate", "must be after startDate", endDate);
        }
    }
}