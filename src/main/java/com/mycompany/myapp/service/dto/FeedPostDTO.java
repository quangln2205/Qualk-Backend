package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;

/** Metadata and interactions only; image bytes are fetched separately. */
public class FeedPostDTO extends PostDTO {

    private long commentsCount;
    private long sharesCount;
    private boolean likedByCurrentUser;
    private boolean sharedByCurrentUser;

    public FeedPostDTO(Long id, String caption, Long userId, String userLogin, Long likesCount, Instant createdDate, String createdBy) {
        setId(id);
        setCaption(caption);
        setUserId(userId);
        setUserLogin(userLogin);
        setLikesCount(likesCount == null ? 0L : likesCount);
        setCreatedDate(createdDate);
        setCreatedBy(createdBy);
    }

    @Override
    @JsonIgnore
    public String getImageData() {
        return null;
    }

    public String getImageUrl() {
        return "api/posts/" + getId() + "/image";
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long value) {
        commentsCount = value;
    }

    public long getSharesCount() {
        return sharesCount;
    }

    public void setSharesCount(long value) {
        sharesCount = value;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean value) {
        likedByCurrentUser = value;
    }

    public boolean isSharedByCurrentUser() {
        return sharedByCurrentUser;
    }

    public void setSharedByCurrentUser(boolean value) {
        sharedByCurrentUser = value;
    }
}
