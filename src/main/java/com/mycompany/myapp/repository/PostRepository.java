package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Post} entity.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
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
