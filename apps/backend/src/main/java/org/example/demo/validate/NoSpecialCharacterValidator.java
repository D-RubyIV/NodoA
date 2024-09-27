package org.example.demo.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoSpecialCharacterValidator implements ConstraintValidator<NoSpecialCharacterConstraint, String> {

    // Cho phép chữ cái tiếng Việt có dấu và các ký tự thông thường (a-z, A-Z, 0-9, khoảng trắng)
    private static final String SPECIAL_CHARACTERS_REGEX = "[^\\p{L}\\p{N} ]"; // \p{L}: chữ cái Unicode, \p{N}: chữ số

    @Override
    public void initialize(NoSpecialCharacterConstraint noSpecialCharacterConstraint) {
        ConstraintValidator.super.initialize(noSpecialCharacterConstraint);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Giá trị null hoặc rỗng là hợp lệ
        }
        // Kiểm tra nếu có ký tự đặc biệt
        return !value.matches(".*" + SPECIAL_CHARACTERS_REGEX + ".*");
    }
}
