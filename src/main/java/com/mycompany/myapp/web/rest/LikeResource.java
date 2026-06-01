package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Like;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.LikeRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.LikeService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link Like}.
 */
@RestController
@RequestMapping("/api/likes")
public class LikeResource {

    private static final Logger LOG = LoggerFactory.getLogger(LikeResource.class);

    private static final String ENTITY_NAME = "like";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LikeService likeService;

    private final UserRepository userRepository;

    public LikeResource(LikeService likeService, UserRepository userRepository) {
        this.likeService = likeService;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST /api/likes/post/{postId}} : Like a post.
     *
     * @param postId the post ID to like
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the like
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/post/{postId}")
    public ResponseEntity<Like> likePost(@PathVariable Long postId) throws URISyntaxException {
        LOG.debug("REST request to like post : {}", postId);

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new BadRequestAlertException(
            "User must be authenticated",
            ENTITY_NAME,
            "notauthenticated"
        ));

        User user = userRepository
            .findOneByLogin(currentLogin)
            .orElseThrow(() -> new BadRequestAlertException("Current user not found", ENTITY_NAME, "usernotfound"));

        Like result = likeService.likePost(user.getId(), postId);
        return ResponseEntity
            .created(new URI("/api/likes/" + result.getId()))
            .header("X-" + applicationName + "-alert", "Post liked with identifier " + postId)
            .body(result);
    }

    /**
     * {@code DELETE /api/likes/post/{postId}} : Unlike a post.
     *
     * @param postId the post ID to unlike
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}
     */
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId) {
        LOG.debug("REST request to unlike post : {}", postId);

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new BadRequestAlertException(
            "User must be authenticated",
            ENTITY_NAME,
            "notauthenticated"
        ));

        User user = userRepository
            .findOneByLogin(currentLogin)
            .orElseThrow(() -> new BadRequestAlertException("Current user not found", ENTITY_NAME, "usernotfound"));

        likeService.unlikePost(user.getId(), postId);
        return ResponseEntity
            .noContent()
            .header("X-" + applicationName + "-alert", "Post unliked with identifier " + postId)
            .build();
    }

    /**
     * {@code POST /api/likes/comment/{commentId}} : Like a comment.
     *
     * @param commentId the comment ID to like
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the like
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/comment/{commentId}")
    public ResponseEntity<Like> likeComment(@PathVariable Long commentId) throws URISyntaxException {
        LOG.debug("REST request to like comment : {}", commentId);

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new BadRequestAlertException(
            "User must be authenticated",
            ENTITY_NAME,
            "notauthenticated"
        ));

        User user = userRepository
            .findOneByLogin(currentLogin)
            .orElseThrow(() -> new BadRequestAlertException("Current user not found", ENTITY_NAME, "usernotfound"));

        Like result = likeService.likeComment(user.getId(), commentId);
        return ResponseEntity
            .created(new URI("/api/likes/" + result.getId()))
            .header("X-" + applicationName + "-alert", "Comment liked with identifier " + commentId)
            .body(result);
    }

    /**
     * {@code DELETE /api/likes/comment/{commentId}} : Unlike a comment.
     *
     * @param commentId the comment ID to unlike
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}
     */
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId) {
        LOG.debug("REST request to unlike comment : {}", commentId);

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new BadRequestAlertException(
            "User must be authenticated",
            ENTITY_NAME,
            "notauthenticated"
        ));

        User user = userRepository
            .findOneByLogin(currentLogin)
            .orElseThrow(() -> new BadRequestAlertException("Current user not found", ENTITY_NAME, "usernotfound"));

        likeService.unlikeComment(user.getId(), commentId);
        return ResponseEntity
            .noContent()
            .header("X-" + applicationName + "-alert", "Comment unliked with identifier " + commentId)
            .build();
    }

    /**
     * {@code GET /api/likes/post/{postId}/count} : Get likes count for a post.
     *
     * @param postId the post ID
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body containing the count
     */
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> getPostLikesCount(@PathVariable Long postId) {
        LOG.debug("REST request to get likes count for post : {}", postId);
        long count = likeService.getPostLikesCount(postId);
        return ResponseEntity.ok(count);
    }

    /**
     * {@code GET /api/likes/comment/{commentId}/count} : Get likes count for a comment.
     *
     * @param commentId the comment ID
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body containing the count
     */
    @GetMapping("/comment/{commentId}/count")
    public ResponseEntity<Long> getCommentLikesCount(@PathVariable Long commentId) {
        LOG.debug("REST request to get likes count for comment : {}", commentId);
        long count = likeService.getCommentLikesCount(commentId);
        return ResponseEntity.ok(count);
    }

    /**
     * {@code GET /api/likes/post/{postId}/check} : Check if current user likes a post.
     *
     * @param postId the post ID to check
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body containing the like status
     */
    @GetMapping("/post/{postId}/check")
    public ResponseEntity<Boolean> isPostLikedByUser(@PathVariable Long postId) {
        LOG.debug("REST request to check if current user likes post : {}", postId);

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentLogin == null) {
            return ResponseEntity.ok(false);
        }

        User user = userRepository.findOneByLogin(currentLogin).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(false);
        }

        boolean isLiked = likeService.isPostLikedByUser(user.getId(), postId);
        return ResponseEntity.ok(isLiked);
    }

    /**
     * {@code GET /api/likes/comment/{commentId}/check} : Check if current user likes a comment.
     *
     * @param commentId the comment ID to check
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body containing the like status
     */
    @GetMapping("/comment/{commentId}/check")
    public ResponseEntity<Boolean> isCommentLikedByUser(@PathVariable Long commentId) {
        LOG.debug("REST request to check if current user likes comment : {}", commentId);

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentLogin == null) {
            return ResponseEntity.ok(false);
        }

        User user = userRepository.findOneByLogin(currentLogin).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(false);
        }

        boolean isLiked = likeService.isCommentLikedByUser(user.getId(), commentId);
        return ResponseEntity.ok(isLiked);
    }
}
