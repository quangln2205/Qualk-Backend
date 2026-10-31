package com.mycompany.myapp.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.repository.*;
import com.mycompany.myapp.repository.projection.*;
import com.mycompany.myapp.service.dto.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.LongStream;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

class FeedServiceTest {

    private final PostRepository posts = mock(PostRepository.class);
    private final LikeRepository likes = mock(LikeRepository.class);
    private final ShareRepository shares = mock(ShareRepository.class);
    private final CommentRepository comments = mock(CommentRepository.class);
    private final FeedService service = new FeedService(posts, likes, shares, comments);

    @BeforeEach
    void login() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("viewer", "unused", List.of()));
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void batchesInteractionsForTheWholePageWithoutReadingImages() throws Exception {
        List<FeedPostRow> data = LongStream.rangeClosed(1, 20)
            .mapToObj(id -> new FeedPostRow(id, "Photo", 7L, "author", 4L, Instant.EPOCH, "author"))
            .toList();
        when(posts.findFeed(isNull(), any())).thenReturn(new PageImpl<>(data));
        when(likes.findLikedPostIds(anyList(), eq("viewer"))).thenReturn(Set.of(1L));
        when(shares.findSharedPostIds(anyList(), eq("viewer"))).thenReturn(Set.of(2L));
        when(shares.countForPosts(anyList())).thenReturn(List.of(new PostInteractionCount(2L, 5L)));
        when(comments.countForPosts(anyList())).thenReturn(List.of(new PostInteractionCount(1L, 3L)));

        Page<FeedPostDTO> result = service.getFeed(null, PageRequest.of(0, 20));
        assertThat(result.getContent().get(0).isLikedByCurrentUser()).isTrue();
        assertThat(result.getContent().get(0).getCommentsCount()).isEqualTo(3);
        assertThat(result.getContent().get(1).isSharedByCurrentUser()).isTrue();
        assertThat(result.getContent().get(1).getSharesCount()).isEqualTo(5);
        assertThat(result.getContent().get(2).getSharesCount()).isZero();
        assertThat(result.getContent().get(2).isLikedByCurrentUser()).isFalse();
        String json = new ObjectMapper().findAndRegisterModules().writeValueAsString(result.getContent());
        assertThat(json).contains("api/posts/1/image").doesNotContain("imageData", "data:image");
        assertThat(json.length()).isLessThan(10000);
        verify(posts).findFeed(isNull(), any());
        verify(likes).findLikedPostIds(anyList(), eq("viewer"));
        verify(shares).findSharedPostIds(anyList(), eq("viewer"));
        verify(shares).countForPosts(anyList());
        verify(comments).countForPosts(anyList());
        verifyNoMoreInteractions(posts, likes, shares, comments);
    }

    @Test
    void capsPageSizeAndUsesStableOrdering() {
        when(posts.findFeed(eq(7L), any())).thenReturn(Page.empty());
        service.getFeed(7L, PageRequest.of(2, 1000));
        ArgumentCaptor<Pageable> page = ArgumentCaptor.forClass(Pageable.class);
        verify(posts).findFeed(eq(7L), page.capture());
        assertThat(page.getValue().getPageSize()).isEqualTo(50);
        assertThat(page.getValue().getPageNumber()).isEqualTo(2);
        assertThat(page.getValue().getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdDate", "id"));
        verifyNoInteractions(likes, shares, comments);
    }

    @Test
    void returnsLightweightCommentsWithViewerLikeState() throws Exception {
        when(comments.findSummariesByPostId(eq(1L), any())).thenReturn(
            new PageImpl<>(List.of(new CommentRow(9L, "Hello", 1L, 7L, "author", 2L, Instant.EPOCH, "author")))
        );
        when(likes.findLikedCommentIds(List.of(9L), "viewer")).thenReturn(Set.of(9L));
        var result = service.getComments(1L, PageRequest.of(0, 20));
        assertThat(result.getContent().get(0).likedByCurrentUser()).isTrue();
        String json = new ObjectMapper().findAndRegisterModules().writeValueAsString(result.getContent());
        assertThat(json).doesNotContain("imageData", "imageUrl", "\"post\":", "\"user\":");
        verifyNoInteractions(posts, shares);
    }

    @Test
    void decodesStoredImageWithoutLoadingTheEntity() {
        when(posts.findImageDataById(1L)).thenReturn(Optional.of("data:image/jpeg;base64,AQID"));
        var result = service.getImage(1L).orElseThrow();
        assertThat(result.bytes()).containsExactly(1, 2, 3);
        assertThat(result.contentType()).isEqualTo("image/jpeg");
        verify(posts).findImageDataById(1L);
        verifyNoMoreInteractions(posts);
        when(posts.findImageDataById(2L)).thenReturn(Optional.empty());
        assertThat(service.getImage(2L)).isEmpty();
    }

    @Test
    void rejectsUnsupportedOrCorruptStoredImages() {
        for (String value : List.of("not an image", "data:image/svg+xml;base64,AQID", "data:image/png;base64,!")) {
            assertThatThrownBy(() -> FeedService.decodeImage(value)).isInstanceOf(ResponseStatusException.class);
        }
    }
}
