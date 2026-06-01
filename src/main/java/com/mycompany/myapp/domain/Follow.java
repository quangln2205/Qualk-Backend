package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Follow entity representing the follower-following relationship between users.
 */
@Entity
@Table(name = "follow")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Follow extends AbstractAuditingEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "follower_id", nullable = false)
    private Long followerId; // User who is following

    @NotNull
    @Column(name = "following_id", nullable = false)
    private Long followingId; // User being followed

    @ManyToOne(optional = false)
    @JoinColumn(name = "follower_id", insertable = false, updatable = false)
    @JsonIgnoreProperties(allowGetters = true)
    private User follower; // User who is following

    @ManyToOne(optional = false)
    @JoinColumn(name = "following_id", insertable = false, updatable = false)
    @JsonIgnoreProperties(allowGetters = true)
    private User following; // User being followed

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowing() {
        return following;
    }

    public void setFollowing(User following) {
        this.following = following;
    }

    @Override
    public String toString() {
        return "Follow{" +
            "id=" + id +
            ", followerId=" + followerId +
            ", followingId=" + followingId +
            ", createdDate=" + getCreatedDate() +
            ", createdBy='" + getCreatedBy() + '\'' +
            '}';
    }
}
