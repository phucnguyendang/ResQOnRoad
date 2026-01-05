package com.rescue.system.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO cho thông tin bình luận/tư vấn trong response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    
    private Long id;
    
    @JsonProperty("post_id")
    private Long postId;
    
    private String content;
    
    @JsonProperty("author")
    private AuthorInfo author;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthorInfo {
        private Long id;
        private String name;
        
        @JsonProperty("avatar_base64")
        private String avatarBase64;
    }
}
