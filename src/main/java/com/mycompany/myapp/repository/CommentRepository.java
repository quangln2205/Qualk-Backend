package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Comment;
import com.mycompany.myapp.repository.projection.CommentRow;
import com.mycompany.myapp.repository.projection.PostInteractionCount;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Comment} entity.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(
        "select new com.mycompany.myapp.repository.projection.PostInteractionCount(c.postId, count(c)) from Comment c where c.postId in :ids group by c.postId"
    )
    List<PostInteractionCount> countForPosts(@Param("ids") List<Long> ids);

    @Query(
        value = "select new com.mycompany.myapp.repository.projection.CommentRow(c.id, c.content, c.postId, c.userId, u.login, c.likesCount, c.createdDate, c.createdBy) " +
        "from Comment c join c.user u where c.postId = :postId",
        countQuery = "select count(c) from Comment c where c.postId = :postId"
    )
    Page<CommentRow> findSummariesByPostId(@Param("postId") Long postId, Pageable pageable);

    long countByUserId(Long userId);

    /**
     * Find all comments for a specific post.
     *
     * @param postId the post ID
     * @param pageable pagination information
     * @return page of comments for the post
     */
    Page<Comment> findByPostIdOrderByCreatedDateDesc(Long postId, Pageable pageable);

    /**
     * Find all comments created by a specific user.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of comments created by the user
     */
    Page<Comment> findByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);

    /**
     * Find all comments for a specific post in descending order by creation date.
     *
     * @param postId the post ID
     * @return list of comments for the post
     */
    List<Comment> findByPostIdOrderByCreatedDateDesc(Long postId);

    /**
     * Count comments for a specific post.
     *
     * @param postId the post ID
     * @return count of comments
     */
    long countByPostId(Long postId);
}
