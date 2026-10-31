package com.mycompany.myapp.repository.projection;

import java.time.Instant;

public record FeedPostRow(Long id, String caption, Long userId, String userLogin, Long likesCount, Instant createdDate, String createdBy) {}
