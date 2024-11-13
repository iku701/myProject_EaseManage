package edu.du.myproject1101_1.repository;

import edu.du.myproject1101_1.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.projectId = :projectId")
    List<ProjectMember> findByProjectId(@Param("projectId") Long projectId);

    @Query("DELETE FROM ProjectMember pm WHERE pm.project.projectId = :projectId AND pm.user.id = :userId")
    void deleteByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
