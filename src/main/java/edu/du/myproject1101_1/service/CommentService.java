package edu.du.myproject1101_1.service;

import edu.du.myproject1101_1.entity.Comment;
import edu.du.myproject1101_1.entity.PublicAnnouncement;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.CommentRepository;
import edu.du.myproject1101_1.repository.PublicAnnouncementRepository;
import edu.du.myproject1101_1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PublicAnnouncementRepository announcementRepository;

    public CommentService(CommentRepository commentRepository,
                          UserRepository userRepository,
                          PublicAnnouncementRepository announcementRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.announcementRepository = announcementRepository;
    }

    public List<Comment> getCommentsByReferenceIdAndType(Long referenceId, String commentType) {
        return commentRepository.findByReferenceIdAndCommentTypeAndParentCommentIsNull(referenceId, commentType);
    }

    public void addComment(String content, Long referenceId, String commentType, Long parentCommentId, String username) {
        // 1. 연관된 공고문 또는 다른 엔티티 확인
        PublicAnnouncement announcement = announcementRepository.findById(referenceId)
                .orElseThrow(() -> new RuntimeException("Public Announcement not found with ID: " + referenceId));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Comment comment = new Comment();
        comment.setUser(currentUser);
        comment.setContent(content);
        comment.setReferenceId(referenceId);
        comment.setCommentType(commentType);

        if (parentCommentId != null) {
            Comment parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("Parent comment not found with ID: " + parentCommentId));
            comment.setParentComment(parentComment);
        }

        commentRepository.save(comment);
    }
}

