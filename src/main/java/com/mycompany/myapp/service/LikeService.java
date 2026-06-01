package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Comment;
import com.mycompany.myapp.domain.Like;
import com.mycompany.myapp.domain.Post;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.CommentRepository;
import com.mycompany.myapp.repository.LikeRepository;
import com.mycompany.myapp.repository.PostRepository;
import com.mycompany.myapp.repository.UserRepository;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing likes on posts and comments.
 */
@Service
@Transactional
public class LikeService {

    private static final Logger LOG = LoggerFactory.getLogger(LikeService.class);

    private final LikeRepository likeRepository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    public LikeService(
        LikeRepository likeRepository,
        UserRepository userRepository,
        PostRepository postRepository,
        CommentRepository commentRepository
    ) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Like a post.
     * 
     * @param userId the user ID
     * @param postId the post ID
     * @return the created like
     */
    public Like likePost(Long userId, Long postId) {
        LOG.debug("User {} is liking post {}", userId, postId);

        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Post post = postRepository
            .findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        // Check if already liked
        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new IllegalArgumentException("You have already liked this post");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setPostId(postId);
        like.setUser(user);
        like.setPost(post);

        Like savedLike = likeRepository.save(like);

        // Update post likes count
        long likesCount = likeRepository.countByPostId(postId);
        post.setLikesCount(likesCount);
        postRepository.save(post);

        return savedLike;
    }

    /**
     * Unlike a post.
     * 
     * @param userId the user ID
     * @param postId the post ID
     */
    public void unlikePost(Long userId, Long postId) {
        LOG.debug("User {} is unliking post {}", userId, postId);

        Like like = likeRepository
            .findByUserIdAndPostId(userId, postId)
            .orElseThrow(() -> new IllegalArgumentException("Like not found"));

        likeRepository.delete(like);

        // Update post likes count
        long likesCount = likeRepository.countByPostId(postId);
        Post post = postRepository
            .findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        post.setLikesCount(likesCount);
        postRepository.save(post);
    }

    /**
     * Like a comment.
     * 
     * @param userId the user ID
     * @param commentId the comment ID
     * @return the created like
     */
    public Like likeComment(Long userId, Long commentId) {
        LOG.debug("User {} is liking comment {}", userId, commentId);

        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Comment comment = commentRepository
            .findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

        // Check if already liked
        if (likeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            throw new IllegalArgumentException("You have already liked this comment");
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setCommentId(commentId);
        like.setUser(user);
        like.setComment(comment);

        Like savedLike = likeRepository.save(like);

        // Update comment likes count
        long likesCount = likeRepository.countByCommentId(commentId);
        comment.setLikesCount(likesCount);
        commentRepository.save(comment);

        return savedLike;
    }

    /**
     * Unlike a comment.
     * 
     * @param userId the user ID
     * @param commentId the comment ID
     */
    public void unlikeComment(Long userId, Long commentId) {
        LOG.debug("User {} is unliking comment {}", userId, commentId);

        Like like = likeRepository
            .findByUserIdAndCommentId(userId, commentId)
            .orElseThrow(() -> new IllegalArgumentException("Like not found"));

        likeRepository.delete(like);

        // Update comment likes count
        long likesCount = likeRepository.countByCommentId(commentId);
        Comment comment = commentRepository
            .findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
        comment.setLikesCount(likesCount);
        commentRepository.save(comment);
    }

    /**
     * Check if a user likes a post.
     * 
     * @param userId the user ID
     * @param postId the post ID
     * @return true if liked
     */
    @Transactional(readOnly = true)
    public boolean isPostLikedByUser(Long userId, Long postId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }

    /**
     * Check if a user likes a comment.
     * 
     * @param userId the user ID
     * @param commentId the comment ID
     * @return true if liked
     */
    @Transactional(readOnly = true)
    public boolean isCommentLikedByUser(Long userId, Long commentId) {
        return likeRepository.existsByUserIdAndCommentId(userId, commentId);
    }

    /**
     * Get likes count for a post.
     * 
     * @param postId the post ID
     * @return count of likes
     */
    @Transactional(readOnly = true)
    public long getPostLikesCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    /**
     * Get likes count for a comment.
     * 
     * @param commentId the comment ID
     * @return count of likes
     */
    @Transactional(readOnly = true)
    public long getCommentLikesCount(Long commentId) {
        return likeRepository.countByCommentId(commentId);
    }

    /**
     * Get all users who liked a post.
     * 
     * @param postId the post ID
     * @return list of likes
     */
    @Transactional(readOnly = true)
    public List<Like> getPostLikes(Long postId) {
        return likeRepository.findByPostId(postId);
    }

    /**
     * Get all users who liked a comment.
     * 
     * @param commentId the comment ID
     * @return list of likes
     */
    @Transactional(readOnly = true)
    public List<Like> getCommentLikes(Long commentId) {
        return likeRepository.findByCommentId(commentId);
    }
}
