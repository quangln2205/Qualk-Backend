package com.mycompany.myapp.web.rest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.service.FeedService;
import com.mycompany.myapp.service.PostService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class FeedResourceTest {

    @Test
    void servesBinaryImagesWithPrivateAuthorizationVaryingCache() throws Exception {
        FeedService service = mock(FeedService.class);
        when(service.getImage(1L)).thenReturn(Optional.of(new FeedService.ImageData(new byte[] { 1, 2, 3 }, "image/jpeg")));
        when(service.getImage(2L)).thenReturn(Optional.empty());
        var mvc = MockMvcBuilders.standaloneSetup(new PostResource(mock(PostService.class), service)).build();
        mvc
            .perform(get("/api/posts/1/image"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("image/jpeg"))
            .andExpect(content().bytes(new byte[] { 1, 2, 3 }))
            .andExpect(header().string("Vary", "Authorization"))
            .andExpect(header().string("Cache-Control", "max-age=300, private"));
        mvc.perform(get("/api/posts/2/image")).andExpect(status().isNotFound());
    }
}
