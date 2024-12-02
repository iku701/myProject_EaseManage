package edu.du.myproject1101_1.repository;

import edu.du.myproject1101_1.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
}
