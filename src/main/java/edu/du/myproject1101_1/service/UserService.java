package edu.du.myproject1101_1.service;

import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // 새 비밀번호가 있는 경우만 암호화
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User saveUserWithoutEncodingPassword(User user) {
        return userRepository.save(user);
    }


    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public boolean sendPasswordResetEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setTokenExpiryDate(LocalDateTime.now().plusHours(1)); // 토큰 만료 시간 설정
            userRepository.save(user);

            // 이메일 전송 로직
            String resetLink = "http://localhost:8080/reset-password?token=" + token;
            String message = "Click the following link to reset your password: " + resetLink;
            emailService.sendEmail(user.getEmail(), "Password Reset Request", message);

            return true;
        }
        return false;
    }

}
