package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Comment;
import com.mycompany.myapp.domain.Post;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.CommentRepository;
import com.mycompany.myapp.repository.LikeRepository;
import com.mycompany.myapp.repository.PostRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing comments.
 */
@Service
@Transactional
public class CommentService {

    private static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final LikeRepository likeRepository;

    public CommentService(
        CommentRepository commentRepository,
        PostRepository postRepository,
        UserRepository userRepository,
        LikeRepository likeRepository
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    /**
     * Create a new comment on a post.
     * 
     * @param postId the post ID
     * @param content the comment content
     * @param userId the user ID creating the comment
     * @return the created comment
     */
    public Comment createComment(Long postId, String content, Long userId) {
        LOG.debug("Creating comment for post {} by user {}", postId, userId);

        Post post = postRepository
            .findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setLikesCount(0L);
        comment.setPost(post);
        comment.setUser(user);

        return commentRepository.save(comment);
    }

    /**
     * Update an existing comment.
     * 
     * @param commentId the comment ID
     * @param content the new content
     * @return the updated comment
     */
    public Comment updateComment(Long commentId, String content) {
        LOG.debug("Updating comment {}", commentId);

        Comment comment = commentRepository
            .findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

        // Check if current user is the comment author
        String currentLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (!comment.getCreatedBy().equals(currentLogin)) {
            throw new IllegalArgumentException("You can only edit your own comments");
        }

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    /**
     * Delete a comment.
     * 
     * @param commentId the comment ID
     */
    public void deleteComment(Long commentId) {
        LOG.debug("Deleting comment {}", commentId);

        Comment comment = commentRepository
            .findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

        // Check if current user is the comment author
        String currentLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (!comment.getCreatedBy().equals(currentLogin)) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }

        // Delete associated likes
        likeRepository.deleteAll(likeRepository.findByCommentId(commentId));

        // Delete the comment
        commentRepository.deleteById(commentId);
    }

    /**
     * Get a comment by ID.
     * 
     * @param commentId the comment ID
     * @return the comment
     */
    @Transactional(readOnly = true)
    public Optional<Comment> getComment(Long commentId) {
        return commentRepository.findById(commentId);
    }

    /**
     * Get all comments for a post.
     * 
     * @param postId the post ID
     * @param pageable pagination information
     * @return page of comments
     */
    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByPost(Long postId, Pageable pageable) {
        LOG.debug("Getting comments for post {}", postId);
        return commentRepository.findByPostIdOrderByCreatedDateDesc(postId, pageable);
    }

    /**
     * Get all comments by a user.
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of comments
     */
    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByUser(Long userId, Pageable pageable) {
        LOG.debug("Getting comments by user {}", userId);
        return commentRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);
    }

    /**
     * Get count of comments for a post.
     * 
     * @param postId the post ID
     * @return count of comments
     */
    @Transactional(readOnly = true)
    public long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }
}
