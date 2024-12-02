package edu.du.myproject1101_1.service;

import edu.du.myproject1101_1.entity.CommunityPost;
import edu.du.myproject1101_1.entity.User;
import edu.du.myproject1101_1.repository.CommunityPostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;

    public CommunityPostService(CommunityPostRepository communityPostRepository) {
        this.communityPostRepository = communityPostRepository;
    }

    // 페이징 처리된 게시글 가져오기
    public Page<CommunityPost> getPagedCommunityPosts(Pageable pageable) {
        Page<CommunityPost> posts = communityPostRepository.findAll(
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        posts.forEach(post -> post.setFormattedCreatedAt(post.getCreatedAt().format(formatter)));
        return posts;
    }

    // 게시글 추가
    public void addCommunityPost(String title, String content, User postedBy) {
        CommunityPost post = new CommunityPost();
        post.setTitle(title);
        post.setContent(content);
        post.setPostedBy(postedBy);
        communityPostRepository.save(post);
    }

    // 게시글 ID로 조회
    public CommunityPost getPostById(Long id) {
        Optional<CommunityPost> post = communityPostRepository.findById(id);
        return post.orElse(null);
    }

    // 게시글 수정
    public void updateCommunityPost(Long id, String title, String content, User currentUser) {
        CommunityPost post = getPostById(id);
        if (post == null) {
            throw new RuntimeException("Community Post not found with ID: " + id);
        }
        if (!post.getPostedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to update this post.");
        }
        post.setTitle(title);
        post.setContent(content);
        communityPostRepository.save(post);
    }

    // 게시글 삭제
    public void deleteCommunityPost(Long id, User currentUser) {
        CommunityPost post = getPostById(id);
        if (post == null) {
            throw new RuntimeException("Community Post not found with ID: " + id);
        }
        if (!post.getPostedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to delete this post.");
        }
        communityPostRepository.delete(post);
    }
}
