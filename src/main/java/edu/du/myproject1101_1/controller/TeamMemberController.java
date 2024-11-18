package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.service.ProjectService;
import edu.du.myproject1101_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class TeamMemberController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/checkTeamMemberEmail")
    public ResponseEntity<Map<String, Boolean>> checkTeamMemberEmail(@RequestParam String email, @RequestParam Long projectId) {
        Map<String, Boolean> response = new HashMap<>();
        Optional<User> user = userService.getUserByEmail(email);

        if (user.isPresent()) {
            Project project = projectService.getProjectById(projectId).orElse(null);
            if (project != null && project.getProjectMembers().stream().anyMatch(member -> member.getUser().getEmail().equals(email))) {
                response.put("exists", true); // 이미 팀 멤버
            } else {
                response.put("valid", true); // 유효한 사용자 추가 가능
            }
        } else {
            response.put("valid", false); // 존재하지 않는 이메일
        }

        return ResponseEntity.ok(response);
    }
}

