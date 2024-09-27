package org.example.demo.validate;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ImageSizeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageSizeConstraint {
    String message() default "Image size exceeds the limit"; // Thông báo lỗi
    long maxSize() default 1048576; // Kích thước tối đa (mặc định 1MB)
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
