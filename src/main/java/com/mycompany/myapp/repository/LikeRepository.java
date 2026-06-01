package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Like;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Like} entity.
 */
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    /**
     * Find a like relationship for a user and post.
     * 
     * @param userId the user ID
     * @param postId the post ID
     * @return optional like relationship
     */
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    /**
     * Find a like relationship for a user and comment.
     * 
     * @param userId the user ID
     * @param commentId the comment ID
     * @return optional like relationship
     */
    Optional<Like> findByUserIdAndCommentId(Long userId, Long commentId);

    /**
     * Count likes for a specific post.
     * 
     * @param postId the post ID
     * @return count of likes
     */
    long countByPostId(Long postId);

    /**
     * Count likes for a specific comment.
     * 
     * @param commentId the comment ID
     * @return count of likes
     */
    long countByCommentId(Long commentId);

    /**
     * Check if a user likes a specific post.
     * 
     * @param userId the user ID
     * @param postId the post ID
     * @return true if like exists
     */
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    /**
     * Check if a user likes a specific comment.
     * 
     * @param userId the user ID
     * @param commentId the comment ID
     * @return true if like exists
     */
    boolean existsByUserIdAndCommentId(Long userId, Long commentId);

    /**
     * Find all likes by a specific user for posts.
     * 
     * @param userId the user ID
     * @return list of likes
     */
    List<Like> findByUserIdAndPostIdIsNotNull(Long userId);

    /**
     * Find all likes by a specific user for comments.
     * 
     * @param userId the user ID
     * @return list of likes
     */
    List<Like> findByUserIdAndCommentIdIsNotNull(Long userId);

    /**
     * Find all likes for a specific post.
     * 
     * @param postId the post ID
     * @return list of likes
     */
    List<Like> findByPostId(Long postId);

    /**
     * Find all likes for a specific comment.
     * 
     * @param commentId the comment ID
     * @return list of likes
     */
    List<Like> findByCommentId(Long commentId);
}
