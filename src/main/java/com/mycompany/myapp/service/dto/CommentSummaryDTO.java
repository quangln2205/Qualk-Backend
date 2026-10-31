package com.mycompany.myapp.service.dto;

import java.time.Instant;

/** Avoid serializing the associated post (including its image) with every comment. */
public record CommentSummaryDTO(
    Long id,
    String content,
    Long postId,
    Long userId,
    String userLogin,
    Long likesCount,
    Instant createdDate,
    String createdBy,
    boolean likedByCurrentUser
) {
    public CommentSummaryDTO(
        Long id,
        String content,
        Long postId,
        Long userId,
        String userLogin,
        Long likesCount,
        Instant createdDate,
        String createdBy
    ) {
        this(id, content, postId, userId, userLogin, likesCount == null ? 0L : likesCount, createdDate, createdBy, false);
    }
    public CommentSummaryDTO withLiked(boolean liked) {
        return new CommentSummaryDTO(id, content, postId, userId, userLogin, likesCount, createdDate, createdBy, liked);
    }
}
