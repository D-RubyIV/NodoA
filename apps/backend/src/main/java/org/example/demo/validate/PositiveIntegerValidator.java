package org.example.demo.validate;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveIntegerValidator implements ConstraintValidator<PositiveIntegerConstraint, Double> {

    @Override
    public void initialize(PositiveIntegerConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value % 1 == 0) {
            return value > 0;
        }
        return false;
    }

}

