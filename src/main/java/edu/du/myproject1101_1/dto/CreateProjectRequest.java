package edu.du.myproject1101_1.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateProjectRequest {
    private String projectName;
    private String projectDescription;
    private String projectStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    // Project members (IDs or usernames to be included as part of the project)
    private List<Long> memberIds; // Assumes that member IDs will be provided as part of the request

    // teamLeader는 현재 로그인된 사용자의 정보로 설정되므로 DTO에서 받지 않습니다.
}
