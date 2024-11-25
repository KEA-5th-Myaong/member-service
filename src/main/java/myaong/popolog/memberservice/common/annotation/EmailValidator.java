package myaong.popolog.memberservice.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import myaong.popolog.memberservice.common.exception.ApiCode;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<EmailFormat, String> {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public void initialize(EmailFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        boolean isValid = email != null && EMAIL_PATTERN.matcher(email).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ApiCode.INVALID_EMAIL_FORMAT.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}
