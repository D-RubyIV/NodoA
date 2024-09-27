package org.example.demo.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageSizeValidator implements ConstraintValidator<ImageSizeConstraint, MultipartFile> {
    private long maxSize;

    @Override
    public void initialize(ImageSizeConstraint constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize(); // Khởi tạo kích thước tối đa
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null) {
            return true; // Không cần validate nếu file null
        }
        return file.getSize() <= maxSize; // Kiểm tra nếu kích thước file nhỏ hơn hoặc bằng maxSize
    }
}
