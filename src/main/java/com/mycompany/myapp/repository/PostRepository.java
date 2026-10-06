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
