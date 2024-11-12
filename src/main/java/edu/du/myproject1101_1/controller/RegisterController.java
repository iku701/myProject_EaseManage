package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.dto.RegisterRequest;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "view/register/register"; // 회원가입 페이지로 이동
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid RegisterRequest registerRequest, BindingResult result, Model model) {
        // 비밀번호 확인 검사
        if (!registerRequest.isPasswordEqualToConfirmPassword()) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match.");
        }

        if (result.hasErrors()) {
            model.addAttribute("registerRequest", registerRequest);
            return "view/register/register"; // 유효성 검사 실패 시 다시 회원가입 페이지로 이동
        }

        // RegisterRequest를 User 엔티티로 변환
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword()); // 실제로는 암호화해야 함
        user.setUsername(registerRequest.getUsername());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setCompany(registerRequest.getCompany());
        user.setAddress(registerRequest.getAddress());
        user.setCity(registerRequest.getCity());
        user.setCountry(registerRequest.getCountry());
        user.setPostalCode(registerRequest.getPostalCode());
        user.setAboutMe(registerRequest.getAboutMe());

        // 사용자 저장
        userService.saveUser(user);

        return "redirect:/login"; // 회원가입 완료 후 로그인 페이지로 리디렉션
    }
}


