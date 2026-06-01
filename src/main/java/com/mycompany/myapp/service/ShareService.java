package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Post;
import com.mycompany.myapp.domain.Share;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.PostRepository;
import com.mycompany.myapp.repository.ShareRepository;
import com.mycompany.myapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing post shares.
 */
@Service
@Transactional
public class ShareService {

    private static final Logger LOG = LoggerFactory.getLogger(ShareService.class);

    private final ShareRepository shareRepository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    public ShareService(ShareRepository shareRepository, UserRepository userRepository, PostRepository postRepository) {
        this.shareRepository = shareRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    /**
     * Share a post.
     * 
     * @param userId the user ID
     * @param postId the post ID
     * @return the created share
     */
    public Share sharePost(Long userId, Long postId) {
        LOG.debug("User {} is sharing post {}", userId, postId);

        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Post post = postRepository
            .findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));

        // Check if already shared
        if (shareRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new IllegalArgumentException("You have already shared this post");
        }

        Share share = new Share();
        share.setUserId(userId);
        share.setPostId(postId);
        share.setUser(user);
        share.setPost(post);

        return shareRepository.save(share);
    }

    /**
     * Unshare a post.
     * 
     * @param userId the user ID
     * @param postId the post ID
     */
    public void unsharePost(Long userId, Long postId) {
        LOG.debug("User {} is unsharing post {}", userId, postId);

        Share share = shareRepository
            .findByUserIdAndPostId(userId, postId)
            .orElseThrow(() -> new IllegalArgumentException("Share not found"));

        shareRepository.delete(share);
    }

    /**
     * Get all shares for a post.
     * 
     * @param postId the post ID
     * @param pageable pagination information
     * @return page of share relationships
     */
    @Transactional(readOnly = true)
    public Page<Share> getPostShares(Long postId, Pageable pageable) {
        LOG.debug("Getting shares for post {}", postId);
        return shareRepository.findByPostIdOrderByCreatedDateDesc(postId, pageable);
    }

    /**
     * Get all shares by a user.
     * 
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of share relationships
     */
    @Transactional(readOnly = true)
    public Page<Share> getSharesByUser(Long userId, Pageable pageable) {
        LOG.debug("Getting shares by user {}", userId);
        return shareRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);
    }

    /**
     * Check if a user has shared a post.
     * 
     * @param userId the user ID
     * @param postId the post ID
     * @return true if shared
     */
    @Transactional(readOnly = true)
    public boolean isPostSharedByUser(Long userId, Long postId) {
        return shareRepository.existsByUserIdAndPostId(userId, postId);
    }

    /**
     * Get count of shares for a post.
     * 
     * @param postId the post ID
     * @return count of shares
     */
    @Transactional(readOnly = true)
    public long getPostSharesCount(Long postId) {
        return shareRepository.countByPostId(postId);
    }
}
