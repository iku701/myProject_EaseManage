package edu.du.myproject1101_1.validator;

import edu.du.myproject1101_1.dto.RegisterRequest;
import edu.du.myproject1101_1.dto.UserProfileUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterRequest.class.equals(clazz) || UserProfileUpdateRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof RegisterRequest) {
            RegisterRequest request = (RegisterRequest) target;

            if (!request.isPasswordEqualToConfirmPassword()) {
                errors.rejectValue("confirmPassword", "password.mismatch");
            }

            if (request.getPassword().length() < 6) {
                errors.rejectValue("password", "password.pattern");
            }
        } else if (target instanceof UserProfileUpdateRequest) {
            UserProfileUpdateRequest request = (UserProfileUpdateRequest) target;

            // UserProfileUpdateRequest에 대한 검증 로직 추가 (패스워드 검증은 생략)
            // 필요 시 다른 필드 검증 로직 추가
            if (request.getUsername() == null || request.getUsername().isEmpty()) {
                errors.rejectValue("username", "username.required");
            }

            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                errors.rejectValue("email", "email.required");
            }
        }
    }
}
