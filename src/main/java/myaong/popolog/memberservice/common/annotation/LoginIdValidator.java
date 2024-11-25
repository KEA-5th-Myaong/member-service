package myaong.popolog.memberservice.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import myaong.popolog.memberservice.common.exception.ApiCode;

import java.util.regex.Pattern;

public class LoginIdValidator implements ConstraintValidator<LoginIdFormat, String>{
    // 소문자, 대문자, 숫자로만 구성 (6~12자)
    private static final String LOGIN_ID_REGEX = "^[a-zA-Z0-9]{6,12}$";
    private static final Pattern LOGIN_ID_PATTERN = Pattern.compile(LOGIN_ID_REGEX);

    @Override
    public void initialize(LoginIdFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String loginId, ConstraintValidatorContext context) {
        boolean isValid = loginId != null && LOGIN_ID_PATTERN.matcher(loginId).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ApiCode.INVALID_LOGIN_ID_FORMAT.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}
