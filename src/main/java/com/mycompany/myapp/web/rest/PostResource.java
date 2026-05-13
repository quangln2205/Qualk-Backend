package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.PostService;
import com.mycompany.myapp.service.dto.PostDTO;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Post}.
 */
@RestController
@RequestMapping("/api/posts")
public class PostResource {

    private static final Logger LOG = LoggerFactory.getLogger(PostResource.class);

    private final PostService postService;

    public PostResource(PostService postService) {
        this.postService = postService;
    }

    /**
     * {@code POST /posts} : Create a new post.
     *
     * @param postDTO the post DTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new post.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO) throws URISyntaxException {
        LOG.debug("REST request to create Post: {}", postDTO);

        PostDTO result = postService.createPost(postDTO);
        return ResponseEntity
            .created(new URI("/api/posts/" + result.getId()))
            .body(result);
    }

    /**
     * {@code GET /posts} : Get all posts with pagination.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all posts.
     */
    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get all Posts");

        final Page<PostDTO> page = postService.getAllPosts(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * {@code GET /posts/:id} : Get the "id" post.
     *
     * @param id the id of the post to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the post.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {
        LOG.debug("REST request to get Post : {}", id);

        Optional<PostDTO> post = postService.getPost(id);
        return ResponseEntity.of(post);
    }

    /**
     * {@code GET /posts/user/:userId} : Get all posts for a specific user.
     *
     * @param userId the user ID.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all posts for the user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getUserPosts(
        @PathVariable Long userId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Posts for user: {}", userId);

        final Page<PostDTO> page = postService.getUserPosts(userId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * {@code PUT /posts/:id} : Update the "id" post.
     *
     * @param id the id of the post to update.
     * @param postDTO the post DTO with updated information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated post.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestBody PostDTO postDTO) {
        LOG.debug("REST request to update Post : {}", id);

        Optional<PostDTO> result = postService.updatePost(id, postDTO);
        return ResponseEntity.of(result);
    }

    /**
     * {@code DELETE /posts/:id} : Delete the "id" post.
     *
     * @param id the id of the post to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        LOG.debug("REST request to delete Post : {}", id);

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code POST /posts/:id/like} : Like a post.
     *
     * @param id the id of the post to like.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated post.
     */
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PostDTO> likePost(@PathVariable Long id) {
        LOG.debug("REST request to like Post : {}", id);

        Optional<PostDTO> result = postService.likePost(id);
        return ResponseEntity.of(result);
    }
}
