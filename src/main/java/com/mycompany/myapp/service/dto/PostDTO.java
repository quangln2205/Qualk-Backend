package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.Post;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO representing a Post, with public attributes.
 */
public class PostDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String imageData; // Base64 encoded image

    private String caption;

    private Long userId;

    private String userLogin; // Username of the creator

    private Long likesCount;

    private Instant createdDate;

    private String createdBy;

    public PostDTO() {
        // Empty constructor needed for Jackson.
    }

    public PostDTO(Post post) {
        this.id = post.getId();
        this.imageData = post.getImageData();
        this.caption = post.getCaption();
        this.userId = post.getUserId();
        this.likesCount = post.getLikesCount();
        this.createdDate = post.getCreatedDate();
        this.createdBy = post.getCreatedBy();
        if (post.getUser() != null) {
            this.userLogin = post.getUser().getLogin();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public Long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PostDTO postDTO = (PostDTO) o;
        if (postDTO.getId() == null || getId() == null) {
            return false;
        }

        return Objects.equals(getId(), postDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "PostDTO{" +
            "id=" + id +
            ", caption='" + caption + '\'' +
            ", userId=" + userId +
            ", userLogin='" + userLogin + '\'' +
            ", likesCount=" + likesCount +
            ", createdDate=" + createdDate +
            '}';
    }
}
