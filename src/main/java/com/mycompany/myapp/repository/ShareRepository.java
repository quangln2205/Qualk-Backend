package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Share;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Share} entity.
 */
@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {
    /**
     * Find a share relationship for a user and post.
     * 
     * @param userId the user ID
     * @param postId the post ID
     * @return optional share relationship
     */
    Optional<Share> findByUserIdAndPostId(Long userId, Long postId);

    /**
     * Count shares for a specific post.
     * 
     * @param postId the post ID
     * @return count of shares
     */
    long countByPostId(Long postId);

    /**
     * Check if a user has shared a specific post.
     * 
     * @param userId the user ID
     * @param postId the post ID
     * @return true if share exists
     */
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    /**
     * Find all shares by a specific user.
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of shares
     */
    Page<Share> findByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);

    /**
     * Find all shares for a specific post.
     * 
     * @param postId the post ID
     * @param pageable pagination information
     * @return page of shares
     */
    Page<Share> findByPostIdOrderByCreatedDateDesc(Long postId, Pageable pageable);

    /**
     * Find all shares for a specific post.
     * 
     * @param postId the post ID
     * @return list of shares
     */
    List<Share> findByPostId(Long postId);

    /**
     * Find all shares by a specific user.
     * 
     * @param userId the user ID
     * @return list of shares
     */
    List<Share> findByUserId(Long userId);
}
