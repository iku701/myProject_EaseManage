package edu.du.myproject1101_1.service;

import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.ProjectMember;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.ProjectRepository;
import edu.du.myproject1101_1.repository.ProjectMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
    }

    public Project updateProject(Long id, Project updatedProject) {
        return projectRepository.findById(id)
                .map(project -> {
                    project.setProjectName(updatedProject.getProjectName());
                    project.setProjectDescription(updatedProject.getProjectDescription());
                    project.setProjectStatus(updatedProject.getProjectStatus());
                    project.setStartDate(updatedProject.getStartDate());
                    project.setEndDate(updatedProject.getEndDate());
                    // 필요한 경우 teamLeader도 업데이트
                    return projectRepository.save(project);
                }).orElseThrow(() -> new RuntimeException("Project not found with id " + id));
    }

    public void addProjectMember(Project project, User user) {
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProject(project);
        projectMember.setUser(user);
        projectMemberRepository.save(projectMember);
    }

    public List<ProjectMember> getProjectMembers(Long projectId) {
        return projectMemberRepository.findByProjectId(projectId);
    }

    public void removeProjectMember(Long projectId, Long userId) {
        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    //
    public List<Project> getProjectsByUser(User user) {
        return projectRepository.findByTeamLeaderOrProjectMembers_User(user);
    }

    //페이징 처리
    public Page<Project> getProjectsByUser(User user, Pageable pageable) {
        return projectRepository.findByTeamLeaderOrProjectMembers_User(user, pageable);
    }

}
