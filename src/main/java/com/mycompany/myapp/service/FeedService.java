package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.*;
import com.mycompany.myapp.repository.projection.PostInteractionCount;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.*;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class FeedService {

    private final PostRepository posts;
    private final LikeRepository likes;
    private final ShareRepository shares;
    private final CommentRepository comments;

    public FeedService(PostRepository posts, LikeRepository likes, ShareRepository shares, CommentRepository comments) {
        this.posts = posts;
        this.likes = likes;
        this.shares = shares;
        this.comments = comments;
    }

    public Page<FeedPostDTO> getFeed(Long userId, Pageable requested) {
        Page<FeedPostDTO> page = posts
            .findFeed(userId, pageRequest(requested))
            .map(row ->
                new FeedPostDTO(
                    row.id(),
                    row.caption(),
                    row.userId(),
                    row.userLogin(),
                    row.likesCount(),
                    row.createdDate(),
                    row.createdBy()
                )
            );
        if (page.isEmpty()) return page;
        List<Long> ids = page.map(FeedPostDTO::getId).getContent();
        String login = SecurityUtils.getCurrentUserLogin().orElse("");
        Set<Long> liked = likes.findLikedPostIds(ids, login);
        Set<Long> shared = shares.findSharedPostIds(ids, login);
        Map<Long, Long> shareCounts = counts(shares.countForPosts(ids));
        Map<Long, Long> commentCounts = counts(comments.countForPosts(ids));
        page.forEach(post -> {
            post.setLikedByCurrentUser(liked.contains(post.getId()));
            post.setSharedByCurrentUser(shared.contains(post.getId()));
            post.setSharesCount(shareCounts.getOrDefault(post.getId(), 0L));
            post.setCommentsCount(commentCounts.getOrDefault(post.getId(), 0L));
        });
        return page;
    }

    public Page<CommentSummaryDTO> getComments(Long postId, Pageable requested) {
        Page<CommentSummaryDTO> page = comments
            .findSummariesByPostId(postId, pageRequest(requested))
            .map(row ->
                new CommentSummaryDTO(
                    row.id(),
                    row.content(),
                    row.postId(),
                    row.userId(),
                    row.userLogin(),
                    row.likesCount(),
                    row.createdDate(),
                    row.createdBy()
                )
            );
        if (page.isEmpty()) return page;
        Set<Long> liked = likes.findLikedCommentIds(
            page.map(CommentSummaryDTO::id).getContent(),
            SecurityUtils.getCurrentUserLogin().orElse("")
        );
        return page.map(comment -> comment.withLiked(liked.contains(comment.id())));
    }

    private Pageable pageRequest(Pageable requested) {
        return PageRequest.of(
            requested.isPaged() ? requested.getPageNumber() : 0,
            requested.isPaged() ? Math.min(requested.getPageSize(), 50) : 20,
            Sort.by(Sort.Direction.DESC, "createdDate", "id")
        );
    }

    private Map<Long, Long> counts(List<PostInteractionCount> rows) {
        return rows.stream().collect(Collectors.toMap(PostInteractionCount::postId, PostInteractionCount::total));
    }

    public record ImageData(byte[] bytes, String contentType) {}

    public Optional<ImageData> getImage(Long id) {
        return posts.findImageDataById(id).map(FeedService::decodeImage);
    }

    static ImageData decodeImage(String data) {
        int separator = data.indexOf(',');
        if (separator < 0 || !data.startsWith("data:image/") || !data.substring(0, separator).endsWith(";base64")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported stored image");
        }
        String type = data.substring(5, separator - 7);
        if (!Set.of("image/jpeg", "image/png", "image/webp", "image/gif").contains(type)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported stored image");
        }
        try {
            return new ImageData(Base64.getDecoder().decode(data.substring(separator + 1)), type);
        } catch (IllegalArgumentException error) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Invalid stored image", error);
        }
    }
}
