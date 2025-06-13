package com.yinya.shortlink.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlRequest {

    /**
     * 原始链接
     */
    @NotBlank
    private String originalUrl;

    /**
     * 自定义短码
     */
    private String customShortCode;

    /**
     * 过期时间，默认七天有效
     */
    private LocalDateTime expiresAt;

    /**
     * 用户ID
     * TODO 假设为通过用户体系内部获取的数据
     */
    private Long userId;

    public LocalDateTime getExpiresAt() {
        if (expiresAt == null) {
            return LocalDateTime.now().plusDays(7);
        }
        return expiresAt;
    }
}