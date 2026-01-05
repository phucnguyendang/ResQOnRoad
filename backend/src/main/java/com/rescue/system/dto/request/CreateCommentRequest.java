package com.rescue.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho yêu cầu thêm bình luận/tư vấn vào bài đăng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {
    
    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Size(min = 3, max = 2000, message = "Nội dung bình luận phải từ 3 đến 2000 ký tự")
    private String content;
}
