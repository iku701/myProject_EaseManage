package edu.du.myproject1101_1.service;

import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.ProjectAnnouncement;
import edu.du.myproject1101_1.entity.ProjectMember;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.ProjectAnnouncementRepository;
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

    @Autowired
    private ProjectAnnouncementRepository projectAnnouncementRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Optional<Project> findByName(String projectName) {
        return projectRepository.findByProjectName(projectName);
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
        projectMember.setRole("Member"); // 기본값 설정
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

    //공지사항 저장
    public void addAnnouncement(ProjectAnnouncement announcement) {
        projectAnnouncementRepository.save(announcement);
    }

    //프로젝트 팀 멤버 삭제
    public void removeProjectMember(Project project, ProjectMember member) {
        project.getProjectMembers().remove(member);
        projectMemberRepository.delete(member);
        projectRepository.save(project);
    }

    // 사용자가 리더이거나 멤버로 포함된 모든 프로젝트 반환(myProject)
    public List<Project> getProjectsByUserInvolved(User user) {
        return projectRepository.findProjectsByUserInvolved(user);
    }

    //사용자가 리더이거나 멤버로 포함된 프로젝트를 제외한 다른 모든 프로젝트 반환(otherProject)
    public List<Project> getProjectsExcludingUserInvolved(User user) {
        List<Project> allProjects = projectRepository.findAll();
        List<Project> userProjects = projectRepository.findByTeamLeaderOrProjectMembers_User(user);
        allProjects.removeAll(userProjects); // 사용자가 참여한 프로젝트 제거
        return allProjects;
    }

    public Page<Project> getPagedProjectsExcludingUserInvolved(User user, Pageable pageable) {
        List<Project> userProjects = projectRepository.findByTeamLeaderOrProjectMembers_User(user);
        List<Long> userProjectIds = userProjects.stream().map(Project::getProjectId).toList();

        return projectRepository.findByProjectIdNotIn(userProjectIds, pageable);
    }

    // 텍스트 길이 제한 메서드
    public String truncateText(String text, int maxLength) {
        if (text.length() > maxLength) {
            return text.substring(0, maxLength) + "...";
        }
        return text;
    }

    // 사용자와 연관된 프로젝트 데이터 가공 예시
    public Page<Project> getPagedTruncatedProjectsExcludingUserInvolved(User user, Pageable pageable, int nameLimit, int statusLimit) {
        Page<Project> projects = getPagedProjectsExcludingUserInvolved(user, pageable);
        projects.forEach(project -> {
            project.setProjectName(truncateText(project.getProjectName(), nameLimit));
            project.setProjectStatus(truncateText(project.getProjectStatus(), statusLimit));
        });
        return projects;
    }



}
