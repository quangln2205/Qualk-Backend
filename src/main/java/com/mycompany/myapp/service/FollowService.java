package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Follow;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.FollowRepository;
import com.mycompany.myapp.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing user follow relationships.
 */
@Service
@Transactional
public class FollowService {

    private static final Logger LOG = LoggerFactory.getLogger(FollowService.class);

    private final FollowRepository followRepository;

    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    /**
     * Follow a user.
     * 
     * @param followerId the follower user ID
     * @param followingId the user ID to follow
     * @return the created follow relationship
     */
    public Follow followUser(Long followerId, Long followingId) {
        LOG.debug("User {} is following user {}", followerId, followingId);

        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("Users cannot follow themselves");
        }

        User follower = userRepository
            .findById(followerId)
            .orElseThrow(() -> new IllegalArgumentException("Follower user not found: " + followerId));
        User following = userRepository
            .findById(followingId)
            .orElseThrow(() -> new IllegalArgumentException("Following user not found: " + followingId));

        // Check if already following
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new IllegalArgumentException("Already following this user");
        }

        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        follow.setFollower(follower);
        follow.setFollowing(following);

        return followRepository.save(follow);
    }

    /**
     * Unfollow a user.
     * 
     * @param followerId the follower user ID
     * @param followingId the user ID to unfollow
     */
    public void unfollowUser(Long followerId, Long followingId) {
        LOG.debug("User {} is unfollowing user {}", followerId, followingId);

        Follow follow = followRepository
            .findByFollowerIdAndFollowingId(followerId, followingId)
            .orElseThrow(() -> new IllegalArgumentException("Follow relationship not found"));

        followRepository.delete(follow);
    }

    /**
     * Get all users that a user is following.
     * 
     * @param followerId the follower user ID
     * @param pageable pagination information
     * @return page of follow relationships
     */
    @Transactional(readOnly = true)
    public Page<Follow> getFollowing(Long followerId, Pageable pageable) {
        LOG.debug("Getting users followed by {}", followerId);
        return followRepository.findByFollowerIdOrderByCreatedDateDesc(followerId, pageable);
    }

    /**
     * Get all followers of a user.
     * 
     * @param followingId the user ID
     * @param pageable pagination information
     * @return page of follow relationships
     */
    @Transactional(readOnly = true)
    public Page<Follow> getFollowers(Long followingId, Pageable pageable) {
        LOG.debug("Getting followers of {}", followingId);
        return followRepository.findByFollowingIdOrderByCreatedDateDesc(followingId, pageable);
    }

    /**
     * Check if a user is following another user.
     * 
     * @param followerId the follower user ID
     * @param followingId the user ID to check
     * @return true if following
     */
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    /**
     * Get count of users that a user is following.
     * 
     * @param followerId the follower user ID
     * @return count of following
     */
    @Transactional(readOnly = true)
    public long getFollowingCount(Long followerId) {
        return followRepository.countByFollowerId(followerId);
    }

    /**
     * Get count of followers of a user.
     * 
     * @param followingId the user ID
     * @return count of followers
     */
    @Transactional(readOnly = true)
    public long getFollowerCount(Long followingId) {
        return followRepository.countByFollowingId(followingId);
    }

    /**
     * Get list of user IDs that a user is following.
     * 
     * @param followerId the follower user ID
     * @return list of user IDs
     */
    @Transactional(readOnly = true)
    public List<Long> getFollowingIds(Long followerId) {
        return followRepository.findByFollowerId(followerId).stream().map(Follow::getFollowingId).collect(Collectors.toList());
    }

    /**
     * Get list of user IDs who are followers of a user.
     * 
     * @param followingId the user ID
     * @return list of user IDs
     */
    @Transactional(readOnly = true)
    public List<Long> getFollowerIds(Long followingId) {
        return followRepository.findByFollowingId(followingId).stream().map(Follow::getFollowerId).collect(Collectors.toList());
    }
}
