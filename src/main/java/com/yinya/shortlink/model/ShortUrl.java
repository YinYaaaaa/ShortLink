package com.yinya.shortlink.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "content_comment")
public class ShortUrl {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String originalUrl;

    private String shortCode;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;
}