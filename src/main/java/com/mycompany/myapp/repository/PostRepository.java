package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Post} entity.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(
        value = "select new com.mycompany.myapp.repository.projection.FeedPostRow(p.id, p.caption, p.userId, u.login, p.likesCount, p.createdDate, p.createdBy) " +
        "from Post p join p.user u where (:userId is null or p.userId = :userId)",
        countQuery = "select count(p) from Post p where (:userId is null or p.userId = :userId)"
    )
    Page<com.mycompany.myapp.repository.projection.FeedPostRow> findFeed(@Param("userId") Long userId, Pageable pageable);

    @Query("select p.imageData from Post p where p.id = :id")
    java.util.Optional<String> findImageDataById(@Param("id") Long id);

    long countByUserId(Long userId);

    @Query("select coalesce(sum(coalesce(p.likesCount, 0)), 0) from Post p where p.userId = :userId")
    long sumLikesCountByUserId(@Param("userId") Long userId);

    @Query("select p.caption from Post p where p.userId = :userId order by p.createdDate desc")
    List<String> findCaptionsByUserIdOrderByCreatedDateDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * Find all posts created by a specific user.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of posts created by the user
     */
    Page<Post> findByUserIdOrderByCreatedDateDesc(Long userId, Pageable pageable);

    /**
     * Find all posts ordered by creation date (for feed).
     *
     * @param pageable pagination information
     * @return page of posts ordered by creation date
     */
    Page<Post> findAllByOrderByCreatedDateDesc(Pageable pageable);

    /**
     * Find all posts created by a user in descending order by creation date.
     *
     * @param userId the user ID
     * @return list of posts
     */
    List<Post> findByUserIdOrderByCreatedDateDesc(Long userId);
}
