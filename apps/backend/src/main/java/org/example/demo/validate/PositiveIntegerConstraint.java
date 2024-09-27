package org.example.demo.validate;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PositiveIntegerValidator.class)

@Target({ ElementType.METHOD, ElementType.FIELD })

@Retention(RetentionPolicy.RUNTIME)

public @interface PositiveIntegerConstraint {
    String message() default "Chữ cái đầu phải viết hoa!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}