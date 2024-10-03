package org.example.demo.validate;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoSpecialCharacterValidator.class)

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })

@Retention(RetentionPolicy.RUNTIME)

public @interface NoSpecialCharacterConstraint {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}