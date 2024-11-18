package edu.du.myproject1101_1.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "project")
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id") // 명확한 컬럼명 지정
    private Long projectId;

    @Column(nullable = false)
    private String projectName;

    @Column(nullable = false, length = 1000)
    private String projectDescription;

    @ManyToOne
    @JoinColumn(name = "team_leader_id", referencedColumnName = "id", nullable = false)
    private User teamLeader;

    @Column(nullable = false)
    private String projectStatus;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> projectMembers; // 새로운 필드 추가

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectAnnouncement> projectAnnouncements; // ProjectAnnouncement 관계 추가
}
