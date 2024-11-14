package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.dto.RegisterRequest;
import edu.du.myproject1101_1.dto.UserProfileUpdateRequest;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.service.UserService;
import edu.du.myproject1101_1.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 사용자 프로필 조회
    @GetMapping("/{id}")
    public String getUserProfile(@PathVariable Long id, Model model) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "view/user/user";
        } else {
            return "view/error/error";
        }
    }

    // 사용자 정보 업데이트
    @PostMapping("/update")
    public String updateUserProfile(@ModelAttribute @Valid UserProfileUpdateRequest userDto, BindingResult result, Model model, Principal principal) {
        if (result.hasErrors()) {
            model.addAttribute("userDto", userDto);
            return "view/user/editProfile";
        }

        Optional<User> userOptional = userService.getUserByEmail(principal.getName());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 이메일이 변경되었는지 확인
            boolean isEmailChanged = !user.getEmail().equals(userDto.getEmail());

            // 사용자 정보 업데이트
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setCompany(userDto.getCompany());
            user.setAddress(userDto.getAddress());
            user.setCity(userDto.getCity());
            user.setCountry(userDto.getCountry());
            user.setPostalCode(userDto.getPostalCode());
            user.setAboutMe(userDto.getAboutMe());

            // 비밀번호는 유지
            userService.saveUserWithoutEncodingPassword(user);

            // 이메일이 변경된 경우에만 로그인 페이지로 리다이렉트
            if (isEmailChanged) {
                return "redirect:/login?emailUpdated=true";
            }

            // 이메일이 변경되지 않은 경우, 프로필 페이지로 리다이렉트
            return "redirect:/view/user";
        } else {
            return "view/error/error";
        }
    }

    // 사용자 프로필 조회 (로그인된 사용자)
    @GetMapping("/view/user")
    public String getLoggedInUserProfile(Model model, Principal principal) {
        Optional<User> user = userService.getUserByEmail(principal.getName());
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("username", user.get().getUsername()); // username 추가
            return "view/user/user";
        } else {
            return "view/error/error";
        }
    }

    // 사용자 프로필 편집 페이지로 이동
    @GetMapping("/editProfile")
    public String editUserProfile(Model model, Principal principal) {
        Optional<User> user = userService.getUserByEmail(principal.getName());
        if (user.isPresent()) {
            UserProfileUpdateRequest userDto = new UserProfileUpdateRequest();
            // User 엔티티의 필드를 DTO로 복사
            userDto.setUsername(user.get().getUsername());
            userDto.setEmail(user.get().getEmail());
            userDto.setFirstName(user.get().getFirstName());
            userDto.setLastName(user.get().getLastName());
            userDto.setCompany(user.get().getCompany());
            userDto.setAddress(user.get().getAddress());
            userDto.setCity(user.get().getCity());
            userDto.setCountry(user.get().getCountry());
            userDto.setPostalCode(user.get().getPostalCode());
            userDto.setAboutMe(user.get().getAboutMe());

            model.addAttribute("username", user.get().getUsername());

            model.addAttribute("userDto", userDto);
            return "view/user/editProfile"; // editProfile.html의 뷰 경로
        } else {
            return "view/error/error";
        }
    }
}
