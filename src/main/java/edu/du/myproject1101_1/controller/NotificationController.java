package edu.du.myproject1101_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NotificationController {

    @GetMapping("/view/notifications")
    public String showNotifications(Model model) {
        // 알림 데이터를 모델에 추가 (예시 데이터, 실제로는 서비스에서 가져와야 함)
        model.addAttribute("notifications", List.of(
                "You have a new message.",
                "Your project deadline is approaching.",
                "A new team member joined your project."
        ));
        return "view/notifications/notification";
    }
}
