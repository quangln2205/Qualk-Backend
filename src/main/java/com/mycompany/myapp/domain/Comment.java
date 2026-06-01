package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Comment entity representing user comments on posts.
 */
@Entity
@Table(name = "comment")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Comment extends AbstractAuditingEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "content", length = 1000, nullable = false)
    private String content; // Comment text

    @NotNull
    @Column(name = "post_id", nullable = false)
    private Long postId; // Reference to the post being commented on

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId; // Reference to the user who created the comment

    @Column(name = "likes_count")
    private Long likesCount = 0L; // Number of likes on this comment

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @JsonIgnoreProperties(allowGetters = true)
    private Post post; // Post being commented on

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties(allowGetters = true)
    private User user; // User who created the comment

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Long likesCount) {
        this.likesCount = likesCount;
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
        return "Comment{" +
            "id=" + id +
            ", content='" + content + '\'' +
            ", postId=" + postId +
            ", userId=" + userId +
            ", likesCount=" + likesCount +
            ", createdDate=" + getCreatedDate() +
            ", createdBy='" + getCreatedBy() + '\'' +
            '}';
    }
}
