package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Share;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.ShareRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.ShareService;
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
 * REST controller for managing {@link Share}.
 */
@RestController
@RequestMapping("/api/shares")
public class ShareResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShareResource.class);

    private static final String ENTITY_NAME = "share";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShareService shareService;

    private final UserRepository userRepository;

    public ShareResource(ShareService shareService, UserRepository userRepository) {
        this.shareService = shareService;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST /api/shares/{postId}} : Share a post.
     *
     * @param postId the post ID to share
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the share relationship
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/{postId}")
    public ResponseEntity<Share> sharePost(@PathVariable Long postId) throws URISyntaxException {
        LOG.debug("REST request to share post : {}", postId);

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new BadRequestAlertException(
            "User must be authenticated",
            ENTITY_NAME,
            "notauthenticated"
        ));

        User user = userRepository
            .findOneByLogin(currentLogin)
            .orElseThrow(() -> new BadRequestAlertException("Current user not found", ENTITY_NAME, "usernotfound"));

        Share result = shareService.sharePost(user.getId(), postId);
        return ResponseEntity
            .created(new URI("/api/shares/" + result.getId()))
            .header("X-" + applicationName + "-alert", "Shared post with identifier " + postId)
            .body(result);
    }

    /**
     * {@code DELETE /api/shares/{postId}} : Unshare a post.
     *
     * @param postId the post ID to unshare
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> unsharePost(@PathVariable Long postId) {
        LOG.debug("REST request to unshare post : {}", postId);

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new BadRequestAlertException(
            "User must be authenticated",
            ENTITY_NAME,
            "notauthenticated"
        ));

        User user = userRepository
            .findOneByLogin(currentLogin)
            .orElseThrow(() -> new BadRequestAlertException("Current user not found", ENTITY_NAME, "usernotfound"));

        shareService.unsharePost(user.getId(), postId);
        return ResponseEntity
            .noContent()
            .header("X-" + applicationName + "-alert", "Unshared post with identifier " + postId)
            .build();
    }

    /**
     * {@code GET /api/shares/post/{postId}/check} : Check if current user has shared a post.
     *
     * @param postId the post ID to check
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body containing the share status
     */
    @GetMapping("/post/{postId}/check")
    public ResponseEntity<Boolean> isPostSharedByUser(@PathVariable Long postId) {
        LOG.debug("REST request to check if current user has shared post : {}", postId);

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentLogin == null) {
            return ResponseEntity.ok(false);
        }

        User user = userRepository.findOneByLogin(currentLogin).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(false);
        }

        boolean isShared = shareService.isPostSharedByUser(user.getId(), postId);
        return ResponseEntity.ok(isShared);
    }

    /**
     * {@code GET /api/shares/post/{postId}/count} : Get count of shares for a post.
     *
     * @param postId the post ID
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and body containing the count
     */
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> getPostSharesCount(@PathVariable Long postId) {
        LOG.debug("REST request to get shares count for post : {}", postId);
        long count = shareService.getPostSharesCount(postId);
        return ResponseEntity.ok(count);
    }

    /**
     * {@code GET /api/shares/post/{postId}} : Get all shares for a post.
     *
     * @param postId the post ID
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of share relationships
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<Share>> getPostShares(@PathVariable Long postId, Pageable pageable) {
        LOG.debug("REST request to get shares for post : {}", postId);
        Page<Share> page = shareService.getPostShares(postId, pageable);
        return ResponseEntity.ok().body(page);
    }

    /**
     * {@code GET /api/shares/user/{userId}} : Get all shares by a user.
     *
     * @param userId the user ID
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of share relationships
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Share>> getSharesByUser(@PathVariable Long userId, Pageable pageable) {
        LOG.debug("REST request to get shares by user : {}", userId);
        Page<Share> page = shareService.getSharesByUser(userId, pageable);
        return ResponseEntity.ok().body(page);
    }
}
