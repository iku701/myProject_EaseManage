package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.dto.CreateProjectRequest;
import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.ProjectMember;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.UserRepository;
import edu.du.myproject1101_1.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String showMyProjectPage(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "6") int size,
                                    Model model, Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("username", user.getUsername());

            Pageable pageable = PageRequest.of(page, size);
            Page<Project> projectPage = projectService.getProjectsByUser(user, pageable);
            model.addAttribute("projects", projectPage.getContent());
            model.addAttribute("currentPage", projectPage.getNumber());
            model.addAttribute("totalPages", projectPage.getTotalPages());
            model.addAttribute("totalItems", projectPage.getTotalElements());
        } else {
            model.addAttribute("username", "Unknown User");
            model.addAttribute("projects", new ArrayList<>()); // 빈 리스트
        }

        return "view/myProject/myProject";
    }


    @GetMapping("/createProject")
    public String showCreateProjectForm(Model model, Principal principal) {
        userRepository.findByEmail(principal.getName())
                .ifPresentOrElse(
                        user -> model.addAttribute("username", user.getUsername()),
                        () -> model.addAttribute("username", "Unknown User")
                );
        model.addAttribute("createProjectRequest", new CreateProjectRequest());
        return "view/myProject/createProjectForm";
    }

    @PostMapping("/createProject")
    public String createProject(@ModelAttribute CreateProjectRequest createProjectRequest, Principal principal) {
        User teamLeader = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // CreateProjectRequest를 Project 엔티티로 변환
        Project project = new Project();
        project.setProjectName(createProjectRequest.getProjectName());
        project.setProjectDescription(createProjectRequest.getProjectDescription());
        project.setProjectStatus(createProjectRequest.getProjectStatus());
        project.setStartDate(createProjectRequest.getStartDate());
        project.setEndDate(createProjectRequest.getEndDate());
        project.setTeamLeader(teamLeader);

        // 프로젝트 멤버 추가
        List<ProjectMember> projectMembers = new ArrayList<>();
        projectMembers.add(createProjectMember(project, teamLeader, "Team Leader"));

        if (createProjectRequest.getMemberIds() != null) {
            createProjectRequest.getMemberIds().forEach(memberId -> {
                userRepository.findById(memberId).ifPresent(member -> {
                    projectMembers.add(createProjectMember(project, member, null));
                });
            });
        }

        project.setProjectMembers(projectMembers);

        // 프로젝트 저장
        projectService.saveProject(project);
        return "redirect:/myProject";
    }

    private ProjectMember createProjectMember(Project project, User user, String role) {
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProject(project);
        projectMember.setUser(user);
        projectMember.setRole(role != null ? role : "Member");
        return projectMember;
    }

    @GetMapping("/projects")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

}