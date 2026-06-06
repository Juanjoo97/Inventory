package com.litethinking.inventory.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ISO4217Validator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ISO4217 {

    String message() default "La moneda debe ser un código ISO 4217 válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}