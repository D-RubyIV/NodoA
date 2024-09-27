package org.example.demo.validate;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ImageFileValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageFileConstraint {
    String message() default "Invalid file type. Only image files are allowed."; // Thông báo lỗi
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
