package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class LoginController {

    // 로그인 페이지 표시
    @GetMapping("/login")
    public String showLoginPage() {
        return "view/login/login"; // 타임리프 뷰 경로
    }
}
