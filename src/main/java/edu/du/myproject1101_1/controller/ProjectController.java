package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import java.util.Optional;

@Controller
public class ProjectController {

    @Autowired
    private UserRepository userRepository; // UserRepository 주입

    @GetMapping("/myProject")
    public String showMyProjectPage(Model model, Principal principal) {
        // Optional 처리
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        // 사용자 이름을 모델에 추가
        if (optionalUser.isPresent()) {
            model.addAttribute("username", optionalUser.get().getUsername());
        } else {
            // 사용자를 찾지 못했을 때 처리 로직 추가 (예: 에러 메시지)
            model.addAttribute("username", "Unknown User");
        }

        return "view/myProject/myProject"; // 타임리프 뷰 경로
    }
}


