package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Comment;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.CommentRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.CommentService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link Comment}.
 */
@RestController
@RequestMapping("/api/comments")
public class CommentResource {

    private static final Logger LOG = LoggerFactory.getLogger(CommentResource.class);

    private static final String ENTITY_NAME = "comment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommentService commentService;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    public CommentResource(CommentService commentService, CommentRepository commentRepository, UserRepository userRepository) {
        this.commentService = commentService;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST /api/comments/{postId}} : Create a new comment on a post.
     *
     * @param postId the post ID to comment on
     * @param commentRequest a map containing the comment content
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new comment
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/{postId}")
    public ResponseEntity<Comment> createComment(@PathVariable Long postId, @Valid @RequestBody Map<String, String> commentRequest)
        throws URISyntaxException {
        LOG.debug("REST request to save Comment for post {}", postId);

        String content = commentRequest.get("content");
        if (content == null || content.trim().isEmpty()) {
            throw new BadRequestAlertException("Comment content cannot be empty", ENTITY_NAME, "contentempty");
        }

        String currentLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new BadRequestAlertException(
            "User must be authenticated",
            ENTITY_NAME,
            "notauthenticated"
        ));

        User user = userRepository
            .findOneByLogin(currentLogin)
            .orElseThrow(() -> new BadRequestAlertException("Current user not found", ENTITY_NAME, "usernotfound"));

        Comment result = commentService.createComment(postId, content, user.getId());
        return ResponseEntity
            .created(new URI("/api/comments/" + result.getId()))
            .header("X-" + applicationName + "-alert", "A new " + ENTITY_NAME + " is created with identifier " + result.getId())
            .body(result);
    }

    /**
     * {@code PUT /api/comments/:id} : Updates an existing comment.
     *
     * @param id the id of the comment to save
     * @param comment the comment to update
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated comment
     */
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @Valid @RequestBody Comment comment) {
        LOG.debug("REST request to update Comment : {}", id);

        if (comment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        if (!id.equals(comment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Comment result = commentService.updateComment(id, comment.getContent());
        return ResponseEntity
            .ok()
            .header("X-" + applicationName + "-alert", ENTITY_NAME + " is updated with identifier " + result.getId())
            .body(result);
    }

    /**
     * {@code GET /api/comments/:id} : Get the "id" comment.
     *
     * @param id the id of the comment to retrieve
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the comment
     */
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable Long id) {
        LOG.debug("REST request to get Comment : {}", id);
        Optional<Comment> comment = commentService.getComment(id);
        return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code GET /api/comments/post/:postId} : Get all comments for a post.
     *
     * @param postId the post ID
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of comments
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<Comment>> getCommentsByPost(@PathVariable Long postId, Pageable pageable) {
        LOG.debug("REST request to get Comments for post : {}", postId);
        Page<Comment> page = commentService.getCommentsByPost(postId, pageable);
        return ResponseEntity.ok().body(page);
    }

    /**
     * {@code GET /api/comments/user/:userId} : Get all comments by a user.
     *
     * @param userId the user ID
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of comments
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Comment>> getCommentsByUser(@PathVariable Long userId, Pageable pageable) {
        LOG.debug("REST request to get Comments by user : {}", userId);
        Page<Comment> page = commentService.getCommentsByUser(userId, pageable);
        return ResponseEntity.ok().body(page);
    }

    /**
     * {@code DELETE /api/comments/:id} : Delete the "id" comment.
     *
     * @param id the id of the comment to delete
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        LOG.debug("REST request to delete Comment : {}", id);
        commentService.deleteComment(id);
        return ResponseEntity
            .noContent()
            .header("X-" + applicationName + "-alert", ENTITY_NAME + " is deleted with identifier " + id)
            .build();
    }
}
