package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Post;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.PostRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.PostDTO;
import com.mycompany.myapp.service.mapper.PostMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing posts.
 */
@Service
@Transactional
public class PostService {

    private static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final PostMapper postMapper;

    public PostService(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
    }

    /**
     * Create a new post.
     * 
     * @param postDTO the post DTO
     * @return the created post DTO
     */
    public PostDTO createPost(PostDTO postDTO) {
        LOG.debug("Creating post: {}", postDTO);

        Post post = new Post();
        post.setImageData(postDTO.getImageData());
        post.setCaption(postDTO.getCaption());

        // Get current user from security context
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Optional<User> user = userRepository.findOneByLogin(currentUserLogin.get());
            if (user.isPresent()) {
                post.setUserId(user.get().getId());
                post.setUser(user.get());
                post.setLikesCount(0L);

                Post savedPost = postRepository.save(post);
                LOG.debug("Created post: {}", savedPost);
                return postMapper.postToPostDTO(savedPost);
            }
        }

        throw new RuntimeException("Unable to create post: Current user not found");
    }

    /**
     * Get all posts with pagination.
     * 
     * @param pageable pagination information
     * @return page of posts
     */
    @Transactional(readOnly = true)
    public Page<PostDTO> getAllPosts(Pageable pageable) {
        LOG.debug("Fetching all posts");
        return postRepository.findAllByOrderByCreatedDateDesc(pageable).map(postMapper::postToPostDTO);
    }

    /**
     * Get posts for a specific user (their profile).
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of posts
     */
    @Transactional(readOnly = true)
    public Page<PostDTO> getUserPosts(Long userId, Pageable pageable) {
        LOG.debug("Fetching posts for user: {}", userId);
        return postRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable).map(postMapper::postToPostDTO);
    }

    /**
     * Get a single post by ID.
     * 
     * @param postId the post ID
     * @return the post DTO
     */
    @Transactional(readOnly = true)
    public Optional<PostDTO> getPost(Long postId) {
        LOG.debug("Fetching post: {}", postId);
        return postRepository.findById(postId).map(postMapper::postToPostDTO);
    }

    /**
     * Update a post (caption and likes).
     * 
     * @param postId the post ID
     * @param postDTO the updated post data
     * @return the updated post DTO
     */
    public Optional<PostDTO> updatePost(Long postId, PostDTO postDTO) {
        LOG.debug("Updating post: {}", postId);

        return postRepository.findById(postId).map(post -> {
            // Verify user owns the post
            Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
            if (currentUserLogin.isPresent() && post.getCreatedBy().equals(currentUserLogin.get())) {
                post = postMapper.postDTOToPostUpdate(postDTO, post);
                Post updatedPost = postRepository.save(post);
                LOG.debug("Updated post: {}", updatedPost);
                return postMapper.postToPostDTO(updatedPost);
            } else {
                throw new RuntimeException("User is not authorized to update this post");
            }
        });
    }

    /**
     * Delete a post by ID.
     * 
     * @param postId the post ID
     */
    public void deletePost(Long postId) {
        LOG.debug("Deleting post: {}", postId);

        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            // Verify user owns the post
            Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
            if (currentUserLogin.isPresent() && post.get().getCreatedBy().equals(currentUserLogin.get())) {
                postRepository.deleteById(postId);
                LOG.debug("Deleted post: {}", postId);
            } else {
                throw new RuntimeException("User is not authorized to delete this post");
            }
        }
    }

    /**
     * Like/Unlike a post (increment likes count).
     * 
     * @param postId the post ID
     * @return the updated post DTO
     */
    public Optional<PostDTO> likePost(Long postId) {
        LOG.debug("Liking post: {}", postId);

        return postRepository.findById(postId).map(post -> {
            post.setLikesCount((post.getLikesCount() != null ? post.getLikesCount() : 0L) + 1);
            Post updatedPost = postRepository.save(post);
            LOG.debug("Post liked: {}", updatedPost);
            return postMapper.postToPostDTO(updatedPost);
        });
    }
}
