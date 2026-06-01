package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Follow;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.FollowService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link Follow}.
 */
@RestController
@RequestMapping("/api/follows")
public class FollowResource {

    private static final Logger LOG = LoggerFactory.getLogger(FollowResource.class);

    private static final String ENTITY_NAME = "follow";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FollowService followService;

    private final UserRepository userRepository;

    public FollowResource(FollowService followService, UserRepository userRepository) {
        this.followService = followService;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST /api/follows/{userId}} : Follow a user.
     *
     * @param userId the user ID to follow
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the follow relationship
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/{userId}")
    public ResponseEntity<Follow> followUser(@PathVariable Long userId) throws URISyntaxException {
        LOG.debug("REST request to follow user : {}", userId);

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new BadRequestAlertException(
            "User must be authenticated",
            ENTITY_NAME,
            "notauthenticated"
        ));

        User follower = userRepository
            .findOneByLogin(currentLogin)
            .orElseThrow(() -> new BadRequestAlertException("Current user not found", ENTITY_NAME, "usernotfound"));

        Follow result = followService.followUser(follower.getId(), userId);
        return ResponseEntity
            .created(new URI("/api/follows/" + result.getId()))
            .header("X-" + applicationName + "-alert", "Following user with identifier " + userId)
            .body(result);
    }

    /**
     * {@code DELETE /api/follows/{userId}} : Unfollow a user.
     *
     * @param userId the user ID to unfollow
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId) {
        LOG.debug("REST request to unfollow user : {}", userId);

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new BadRequestAlertException(
            "User must be authenticated",
            ENTITY_NAME,
            "notauthenticated"
        ));

        User follower = userRepository
            .findOneByLogin(currentLogin)
            .orElseThrow(() -> new BadRequestAlertException("Current user not found", ENTITY_NAME, "usernotfound"));

        followService.unfollowUser(follower.getId(), userId);
        return ResponseEntity
            .noContent()
            .header("X-" + applicationName + "-alert", "Unfollowed user with identifier " + userId)
            .build();
    }

    /**
     * {@code GET /api/follows/following/{userId}} : Get all users followed by a user.
     *
     * @param userId the user ID
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of follow relationships
     */
    @GetMapping("/following/{userId}")
    public ResponseEntity<Page<Follow>> getFollowing(@PathVariable Long userId, Pageable pageable) {
        LOG.debug("REST request to get following list for user : {}", userId);
        Page<Follow> page = followService.getFollowing(userId, pageable);
        return ResponseEntity.ok().body(page);
    }

    /**
     * {@code GET /api/follows/followers/{userId}} : Get all followers of a user.
     *
     * @param userId the user ID
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of follow relationships
     */
    @GetMapping("/followers/{userId}")
    public ResponseEntity<Page<Follow>> getFollowers(@PathVariable Long userId, Pageable pageable) {
        LOG.debug("REST request to get followers for user : {}", userId);
        Page<Follow> page = followService.getFollowers(userId, pageable);
        return ResponseEntity.ok().body(page);
    }

    /**
     * {@code GET /api/follows/check/{userId}} : Check if current user follows a user.
     *
     * @param userId the user ID to check
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body containing the follow status
     */
    @GetMapping("/check/{userId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long userId) {
        LOG.debug("REST request to check if current user is following : {}", userId);

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentLogin == null) {
            return ResponseEntity.ok(false);
        }

        User follower = userRepository.findOneByLogin(currentLogin).orElse(null);
        if (follower == null) {
            return ResponseEntity.ok(false);
        }

        boolean isFollowing = followService.isFollowing(follower.getId(), userId);
        return ResponseEntity.ok(isFollowing);
    }

    /**
     * {@code GET /api/follows/count/following/{userId}} : Get count of users followed by a user.
     *
     * @param userId the user ID
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body containing the count
     */
    @GetMapping("/count/following/{userId}")
    public ResponseEntity<Long> getFollowingCount(@PathVariable Long userId) {
        LOG.debug("REST request to get following count for user : {}", userId);
        long count = followService.getFollowingCount(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * {@code GET /api/follows/count/followers/{userId}} : Get count of followers of a user.
     *
     * @param userId the user ID
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body containing the count
     */
    @GetMapping("/count/followers/{userId}")
    public ResponseEntity<Long> getFollowerCount(@PathVariable Long userId) {
        LOG.debug("REST request to get followers count for user : {}", userId);
        long count = followService.getFollowerCount(userId);
        return ResponseEntity.ok(count);
    }
}
