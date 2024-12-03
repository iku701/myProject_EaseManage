```mermaid
erDiagram
    USER ||--o{ PROJECT : leads
    PROJECT ||--o{ PROJECT_MEMBER : has
    USER ||--o{ PROJECT_MEMBER : joins
    PROJECT ||--o{ PROJECT_ANNOUNCEMENT : has
    PROJECT ||--o{ PUBLIC_ANNOUNCEMENT : has
    USER ||--o{ COMMENT : writes
    USER ||--o{ COMMUNITY_POST : posts
    COMMENT ||--o{ COMMENT : replies

    USER {
        Long id PK
        String email "unique"
        String username
        String password
        String firstName
        String lastName
        String company
        String address
        String city
        String country
        String postalCode
        String aboutMe
        String role
    }

    PROJECT {
        Long projectId PK
        String projectName
        String projectDescription
        Long teamLeaderId FK
        String projectStatus
        LocalDate startDate
        LocalDate endDate
    }

    PROJECT_MEMBER {
        Long projectMemberId PK
        Long projectId FK
        Long userId FK
        String role
    }

    PROJECT_ANNOUNCEMENT {
        Long id PK
        Long projectId FK
        String title
        String content
        Long authorId FK
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }

    COMMENT {
        Long id PK
        Long userId FK
        String content
        String commentType
        Long referenceId
        Long parentCommentId FK
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }

    PUBLIC_ANNOUNCEMENT {
        Long id PK
        String title
        String content
        Long projectId FK
        Long postedBy FK
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }

    COMMUNITY_POST {
        Long id PK
        String title
        String content
        Long postedBy FK
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }
