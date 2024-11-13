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
    @Query("SELECT p FROM Project p LEFT JOIN p.projectMembers pm WHERE p.teamLeader = :user OR pm.user = :user")
    List<Project> findByTeamLeaderOrProjectMembers_User(@Param("user") User user);

    @Query("SELECT p FROM Project p LEFT JOIN p.projectMembers pm WHERE p.teamLeader = :user OR pm.user = :user")
    Page<Project> findByTeamLeaderOrProjectMembers_User(User user, Pageable pageable);
}

