package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Share entity representing user shares of posts.
 */
@Entity
@Table(name = "share")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Share extends AbstractAuditingEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "post_id", nullable = false)
    private Long postId; // Reference to the post being shared

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId; // Reference to the user who shared the post

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @JsonIgnoreProperties(allowGetters = true)
    private Post post; // Post being shared

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties(allowGetters = true)
    private User user; // User who shared the post

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Share{" +
            "id=" + id +
            ", postId=" + postId +
            ", userId=" + userId +
            ", createdDate=" + getCreatedDate() +
            ", createdBy='" + getCreatedBy() + '\'' +
            '}';
    }
}
