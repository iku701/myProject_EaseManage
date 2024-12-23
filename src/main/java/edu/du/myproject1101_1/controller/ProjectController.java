package edu.du.myproject1101_1.controller;

import edu.du.myproject1101_1.dto.CreateProjectRequest;
import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.ProjectMember;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.exception.UserNotFoundException;
import edu.du.myproject1101_1.exception.UnauthorizedAccessException;
import edu.du.myproject1101_1.repository.UserRepository;
import edu.du.myproject1101_1.service.ProjectService;
import edu.du.myproject1101_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ProjectController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

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
                .orElseThrow(() -> new UserNotFoundException("User not found"));

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
        projectMember.setRole(role != null ? role : "Member"); // 기본값으로 "Member" 설정
        return projectMember;
    }

    @GetMapping("/projects")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    //프로젝트별 정보 확인
    @GetMapping("/myProjectForm/{id}")
    public String showProjectDetails(@PathVariable Long id, Model model, Principal principal) {
        Optional<Project> projectOptional = projectService.getProjectById(id);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            model.addAttribute("project", project);

            // 로그인한 사용자 정보 추가
            User currentUser = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 역할에 따라 적절한 페이지 반환
            if (project.getTeamLeader().equals(currentUser)) {
                // 사용자가 프로젝트의 팀 리더인 경우
                model.addAttribute("username", currentUser.getUsername());
                return "view/myProject/myProjectForm"; // 팀 리더용 페이지
            } else {
                // 사용자가 팀 멤버인 경우
                boolean isMember = project.getProjectMembers().stream()
                        .anyMatch(member -> member.getUser().equals(currentUser));
                if (isMember) {
                    model.addAttribute("username", currentUser.getUsername());
                    return "view/myProject/myProjectFormByTeamMember"; // 팀 멤버용 페이지
                } else {
                    model.addAttribute("errorMessage", "You do not have permission to view this project.");
                    return "view/error/error"; // 권한이 없는 경우 에러 페이지
                }
            }
        } else {
            model.addAttribute("errorMessage", "Project not found.");
            return "view/error/error"; // 프로젝트를 찾지 못했을 경우 에러 페이지
        }
    }


    @PostMapping("/addTeamMember")
    public String addTeamMember(@RequestParam Long projectId, @RequestParam String email, Principal principal, RedirectAttributes redirectAttributes) {
        // 현재 로그인된 사용자가 프로젝트 팀 리더인지 확인
        User currentUser = userService.getUserByEmail(principal.getName()).orElseThrow(() -> new UserNotFoundException("User not found"));
        Project project = projectService.getProjectById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getTeamLeader().equals(currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to add team members.");
            return "redirect:/myProjectForm/" + projectId;
        }

        // 팀원 추가 시 이메일 확인
        Optional<User> optionalNewMember = userService.getUserByEmail(email);
        if (optionalNewMember.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User with the provided email does not exist.");
            return "redirect:/myProjectForm/" + projectId;
        }

        User newMember = optionalNewMember.get();

        // 중복 체크
        if (project.getProjectMembers().stream().anyMatch(member -> member.getUser().equals(newMember))) {
            redirectAttributes.addFlashAttribute("errorMessage", "This user is already a team member.");
            return "redirect:/myProjectForm/" + projectId;
        }

        projectService.addProjectMember(project, newMember);
        redirectAttributes.addFlashAttribute("successMessage", "Team member added successfully.");

        return "redirect:/myProjectForm/" + projectId;
    }

    @GetMapping("/editProjectForm/{id}")
    public String showEditProjectForm(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        Optional<Project> projectOptional = projectService.getProjectById(id);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            User currentUser = userService.getUserByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (project.getTeamLeader().equals(currentUser)) {
                model.addAttribute("project", project);
                model.addAttribute("username", currentUser.getUsername());
                return "view/myProject/editProjectForm";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "You do not have permission to edit this project.");
                return "redirect:/myProjectForm/" + id;
            }
        } else {
            model.addAttribute("errorMessage", "Project not found.");
            return "view/error/error";
        }
    }

    @PostMapping("/updateProject")
    public String updateProject(@ModelAttribute Project updatedProject, RedirectAttributes redirectAttributes) {
        try {
            projectService.updateProject(updatedProject.getProjectId(), updatedProject);
            redirectAttributes.addFlashAttribute("successMessage", "Project updated successfully.");
            return "redirect:/myProjectForm/" + updatedProject.getProjectId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/editProjectForm/" + updatedProject.getProjectId();
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        });
    }
    //프로젝트 팀 멤버 삭제
    @DeleteMapping("/removeTeamMember")
    public ResponseEntity<String> removeTeamMember(@RequestParam Long projectId, @RequestParam String email, Principal principal) {
        // 현재 로그인한 사용자가 프로젝트의 팀 리더인지 확인
        User currentUser = userService.getUserByEmail(principal.getName()).orElseThrow(() -> new UserNotFoundException("User not found"));
        Project project = projectService.getProjectById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getTeamLeader().equals(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to remove team members.");
        }

        // 삭제할 팀 멤버 찾기
        Optional<ProjectMember> memberToRemove = project.getProjectMembers().stream()
                .filter(member -> member.getUser().getEmail().equals(email))
                .findFirst();

        if (memberToRemove.isPresent()) {
            projectService.removeProjectMember(project, memberToRemove.get());
            return ResponseEntity.ok("Member removed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found.");
        }
    }

}
