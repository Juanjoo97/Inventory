package com.litethinking.inventory.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Currency;

public class ISO4217Validator
        implements ConstraintValidator<ISO4217, String> {

    @Override
    public boolean isValid(String value,
                           ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return true;
        }

        try {
            Currency.getInstance(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}