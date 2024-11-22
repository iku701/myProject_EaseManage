package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.entity.PublicAnnouncement;
import edu.du.myproject1101_1.service.PublicAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class NotificationController {

    @Autowired
    private PublicAnnouncementService announcementService;

    @GetMapping("/view/notifications")
    public String showNotifications(Model model) {
        // 알림 데이터를 모델에 추가 (예시 데이터)
        model.addAttribute("notifications", List.of(
                "You have a new message.",
                "Your project deadline is approaching.",
                "A new team member joined your project."
        ));

        // 공고 데이터 추가
        List<PublicAnnouncement> announcements = announcementService.getAllAnnouncements();
        model.addAttribute("announcements", announcements);

        // 자유게시판 관련 데이터 추가 (여기에 실제 구현이 필요하면 동일한 방식으로 처리)
        // model.addAttribute("communityPosts", communityService.getAllPosts());

        return "view/notifications/notification";
    }
}
