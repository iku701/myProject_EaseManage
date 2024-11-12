package edu.du.myproject1101_1.validator;

import edu.du.myproject1101_1.dto.RegisterRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterRequest request = (RegisterRequest) target;

        if (!request.isPasswordEqualToConfirmPassword()) {
            errors.rejectValue("confirmPassword", "password.mismatch");
        }

        if (request.getPassword().length() < 6) {
            errors.rejectValue("password", "password.pattern");
        }
    }
}

