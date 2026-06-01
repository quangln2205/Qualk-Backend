package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Follow;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Follow} entity.
 */
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    /**
     * Find all users followed by a specific user.
     * 
     * @param followerId the follower user ID
     * @param pageable pagination information
     * @return page of users being followed
     */
    Page<Follow> findByFollowerIdOrderByCreatedDateDesc(Long followerId, Pageable pageable);

    /**
     * Find all followers of a specific user.
     * 
     * @param followingId the user ID being followed
     * @param pageable pagination information
     * @return page of followers
     */
    Page<Follow> findByFollowingIdOrderByCreatedDateDesc(Long followingId, Pageable pageable);

    /**
     * Find a follow relationship between two users.
     * 
     * @param followerId the follower user ID
     * @param followingId the user ID being followed
     * @return optional follow relationship
     */
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * Count how many users are followed by a specific user.
     * 
     * @param followerId the follower user ID
     * @return count of following
     */
    long countByFollowerId(Long followerId);

    /**
     * Count how many followers a specific user has.
     * 
     * @param followingId the user ID being followed
     * @return count of followers
     */
    long countByFollowingId(Long followingId);

    /**
     * Check if a user follows another user.
     * 
     * @param followerId the follower user ID
     * @param followingId the user ID being followed
     * @return true if follow relationship exists
     */
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * Find all users followed by a specific user.
     * 
     * @param followerId the follower user ID
     * @return list of follow relationships
     */
    List<Follow> findByFollowerId(Long followerId);

    /**
     * Find all followers of a specific user.
     * 
     * @param followingId the user ID being followed
     * @return list of follow relationships
     */
    List<Follow> findByFollowingId(Long followingId);
}
