package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.dto.RegisterRequest;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.service.UserService;
import edu.du.myproject1101_1.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired // 필드 주입 또는 생성자 주입
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 사용자 프로필 조회
    @GetMapping("/{id}")
    public String getUserProfile(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "view/user/user"; // 새 경로에 맞춘 뷰 경로
        } else {
            return "view/error/error"; // 에러 페이지 경로는 필요에 맞게 수정하세요
        }
    }

    // 사용자 정보 업데이트
    @PostMapping("/update")
    public String updateUserProfile(@ModelAttribute @Valid User user, BindingResult result, Model model) {
        userValidator.validate(user, result);
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "view/user/user";
        }
        userService.saveUser(user);
        return "redirect:/user/" + user.getId();
    }

}
