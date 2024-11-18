package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.UserRepository;
import edu.du.myproject1101_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class PasswordController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "view/login/forgot-password"; // forgot-password.html 파일을 반환
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            boolean emailSent = userService.sendPasswordResetEmail(email);
            if (emailSent) {
                redirectAttributes.addFlashAttribute("successMessage", "Password reset email sent successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "No account found with that email address.");
            }
        } catch (Exception e) {
            logger.error("Error while sending password reset email for {}", email, e);
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while processing your request. Please try again later.");
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam String token, Model model) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isPresent() && userOptional.get().getTokenExpiryDate().isAfter(LocalDateTime.now())) {
            model.addAttribute("token", token);
            return "view/login/reset-password"; // 비밀번호 재설정 폼 페이지
        }
        model.addAttribute("errorMessage", "Invalid or expired token.");
        return "view/error/error";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String token, @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isPresent() && userOptional.get().getTokenExpiryDate().isAfter(LocalDateTime.now())) {
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword)); // 비밀번호 암호화
            user.setResetToken(null);
            user.setTokenExpiryDate(null);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Your password has been reset successfully.");
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Invalid or expired token.");
        return "redirect:/reset-password";
    }

}
