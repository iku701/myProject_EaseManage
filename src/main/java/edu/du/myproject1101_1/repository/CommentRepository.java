package edu.du.myproject1101_1.repository;

import edu.du.myproject1101_1.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByReferenceIdAndCommentTypeAndParentCommentIsNull(Long referenceId, String commentType);
}
