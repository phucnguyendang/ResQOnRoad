package com.rescue.system.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO cho thông tin bài đăng trong response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    
    private Long id;
    
    private String title;
    
    private String content;
    
    @JsonProperty("author")
    private AuthorInfo author;
    
    @JsonProperty("comment_count")
    private Integer commentCount;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    private String status;
    
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
