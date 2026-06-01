package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Like entity representing user likes on posts or comments.
 */
@Entity
@Table(name = "jhi_like")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Like extends AbstractAuditingEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId; // User who liked

    @Column(name = "post_id")
    private Long postId; // Post being liked (nullable if liking a comment)

    @Column(name = "comment_id")
    private Long commentId; // Comment being liked (nullable if liking a post)

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties(allowGetters = true)
    private User user; // User who liked

    @ManyToOne(optional = true)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @JsonIgnoreProperties(allowGetters = true)
    private Post post; // Post being liked

    @ManyToOne(optional = true)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    @JsonIgnoreProperties(allowGetters = true)
    private Comment comment; // Comment being liked

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Like{" +
            "id=" + id +
            ", userId=" + userId +
            ", postId=" + postId +
            ", commentId=" + commentId +
            ", createdDate=" + getCreatedDate() +
            ", createdBy='" + getCreatedBy() + '\'' +
            '}';
    }
}
