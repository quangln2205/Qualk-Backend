package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Post;
import com.mycompany.myapp.service.dto.PostDTO;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Mapper for the entity {@link Post} and its DTO called {@link PostDTO}.
 */
@Service
public class PostMapper {

    public List<PostDTO> postsToPostDTOs(List<Post> posts) {
        return posts.stream().filter(Objects::nonNull).map(this::postToPostDTO).toList();
    }

    public PostDTO postToPostDTO(Post post) {
        return new PostDTO(post);
    }

    public List<Post> postDTOsToPosts(List<PostDTO> postDTOs) {
        return postDTOs.stream().filter(Objects::nonNull).map(this::postDTOToPost).toList();
    }

    public Post postDTOToPost(PostDTO postDTO) {
        if (postDTO == null) {
            return null;
        }

        Post post = new Post();
        post.setId(postDTO.getId());
        post.setImageData(postDTO.getImageData());
        post.setCaption(postDTO.getCaption());
        post.setUserId(postDTO.getUserId());
        post.setLikesCount(postDTO.getLikesCount());

        return post;
    }

    public Post postDTOToPostUpdate(PostDTO postDTO, Post post) {
        if (postDTO == null) {
            return post;
        }

        if (postDTO.getCaption() != null) {
            post.setCaption(postDTO.getCaption());
        }
        if (postDTO.getLikesCount() != null) {
            post.setLikesCount(postDTO.getLikesCount());
        }

        return post;
    }
}
