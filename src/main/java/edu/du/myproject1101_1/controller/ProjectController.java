package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.dto.CreateProjectRequest;
import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.ProjectMember;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.UserRepository;
import edu.du.myproject1101_1.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ProjectController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/myProject")
    public String showMyProjectPage(Model model, Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        if (optionalUser.isPresent()) {
            model.addAttribute("username", optionalUser.get().getUsername());
        } else {
            model.addAttribute("username", "Unknown User");
        }

        return "view/myProject/myProject";
    }

    @GetMapping("/createProject")
    public String showCreateProjectForm(Model model, Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());
        if (optionalUser.isPresent()) {
            model.addAttribute("username", optionalUser.get().getUsername());
        } else {
            model.addAttribute("username", "Unknown User");
        }
        model.addAttribute("createProjectRequest", new CreateProjectRequest());
        return "view/myProject/createProjectForm";
    }

    @PostMapping("/createProject")
    public String createProject(@ModelAttribute CreateProjectRequest createProjectRequest, Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        if (optionalUser.isPresent()) {
            User teamLeader = optionalUser.get();

            // CreateProjectRequest를 Project 엔티티로 변환
            Project project = new Project();
            project.setProjectName(createProjectRequest.getProjectName());
            project.setProjectDescription(createProjectRequest.getProjectDescription());
            project.setProjectStatus(createProjectRequest.getProjectStatus());
            project.setStartDate(createProjectRequest.getStartDate());
            project.setEndDate(createProjectRequest.getEndDate());
            project.setTeamLeader(teamLeader);

            // 프로젝트 멤버 추가
            List<Long> memberIds = createProjectRequest.getMemberIds();
            List<ProjectMember> projectMembers = new ArrayList<>();

            // 팀 리더도 프로젝트 멤버로 추가
            ProjectMember leaderMember = new ProjectMember();
            leaderMember.setProject(project);
            leaderMember.setUser(teamLeader);
            leaderMember.setRole("Team Leader"); // 팀 리더 역할 설정
            projectMembers.add(leaderMember);

            if (memberIds != null) {
                for (Long memberId : memberIds) {
                    Optional<User> memberOptional = userRepository.findById(memberId);
                    if (memberOptional.isPresent()) {
                        ProjectMember projectMember = new ProjectMember();
                        projectMember.setProject(project);
                        projectMember.setUser(memberOptional.get());
                        projectMembers.add(projectMember);
                    }
                }
            }
            project.setProjectMembers(projectMembers);


            // 프로젝트 저장
            projectService.saveProject(project);
            return "redirect:/myProject";
        } else {
            // 예외 처리: 사용자 찾기 실패 시 적절한 메시지를 설정
            return "redirect:/error";
        }
    }

    @GetMapping("/projects")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }
}
