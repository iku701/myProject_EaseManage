package edu.du.myproject1101_1.service;

import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
