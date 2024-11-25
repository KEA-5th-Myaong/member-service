package myaong.popolog.memberservice.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import myaong.popolog.memberservice.common.exception.ApiCode;

import java.util.regex.Pattern;

public class PasswordFormatValidator implements ConstraintValidator<PasswordFormat, String> {
    // 최소 하나의 영문자(소문자 또는 대문자), 최소 하나의 숫자, 최소 하나의 특수문자(@#$%^&+=!)를 포함하는 8~20자
    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,20}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    @Override
    public void initialize(PasswordFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        boolean isValid = password != null && PASSWORD_PATTERN.matcher(password).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ApiCode.INVALID_PASSWORD_FORMAT.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}
