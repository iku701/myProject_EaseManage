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

    // 댓글 리스트 가져오기
    public List<Comment> getCommentsByReferenceIdAndType(Long referenceId, String commentType) {
        return commentRepository.findByReferenceIdAndCommentTypeAndParentCommentIsNull(referenceId, commentType);
    }

    // 댓글 추가
    public void addComment(String content, Long referenceId, String commentType, Long parentCommentId, String username) {
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

    // 댓글 삭제 (대댓글 포함)
    public void deleteComment(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));

        // 권한 확인: 댓글 작성자인지 확인
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to delete this comment.");
        }

        // 대댓글도 함께 삭제
        deleteReplies(comment);

        // 부모 댓글 삭제
        commentRepository.delete(comment);
    }

    // 대댓글 삭제
    private void deleteReplies(Comment parentComment) {
        if (parentComment.getReplies() != null && !parentComment.getReplies().isEmpty()) {
            for (Comment reply : parentComment.getReplies()) {
                deleteReplies(reply); // 대댓글의 대댓글 처리
                commentRepository.delete(reply); // 대댓글 삭제
            }
        }
    }

    // 댓글 수정
    public void updateComment(Long commentId, String content, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));

        // 권한 확인: 댓글 작성자인지 확인
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to update this comment.");
        }

        // 내용 업데이트 및 저장
        comment.setContent(content);
        commentRepository.save(comment);
    }
}
