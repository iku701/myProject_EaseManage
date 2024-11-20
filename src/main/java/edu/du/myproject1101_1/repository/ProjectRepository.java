package edu.du.myproject1101_1.repository;

import edu.du.myproject1101_1.entity.Project;
import edu.du.myproject1101_1.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // 기존 메서드
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.projectMembers pm WHERE p.teamLeader = :user OR pm.user = :user")
    List<Project> findByTeamLeaderOrProjectMembers_User(@Param("user") User user);

    // 페이징 지원 메서드
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.projectMembers pm WHERE p.teamLeader = :user OR pm.user = :user")
    Page<Project> findByTeamLeaderOrProjectMembers_User(User user, Pageable pageable);

    // 사용자가 리더이거나 멤버로 포함된 모든 프로젝트 반환 메서드
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.projectMembers pm WHERE p.teamLeader = :user OR pm.user = :user")
    List<Project> findProjectsByUserInvolved(@Param("user") User user);

    // 프로젝트 ID 목록에 포함되지 않은 프로젝트를 페이징 처리하여 반환
    Page<Project> findByProjectIdNotIn(List<Long> projectIds, Pageable pageable);

}



