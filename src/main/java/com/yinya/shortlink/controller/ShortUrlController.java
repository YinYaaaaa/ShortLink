package com.yinya.shortlink.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yinya.shortlink.request.PageQuery;
import com.yinya.shortlink.request.ShortUrlRequest;
import com.yinya.shortlink.response.ShortUrlResponse;
import com.yinya.shortlink.service.ShortUrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shorten")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    /**
     * 创建短链接
     */
    @PostMapping("/insert")
    public ShortUrlResponse createShortUrl(@Valid @RequestBody ShortUrlRequest request) {
        return shortUrlService.createShortUrl(request);
    }

    /**
     * 查询当前用户的短链接
     */
    @PostMapping("/list")
    public Page<ShortUrlResponse> getShortUrls(@RequestBody PageQuery request) {
        return shortUrlService.getShortUrls(request);
    }

    /**
     * 删除短链接
     */
    @GetMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShortUrl(@NotBlank String shortCode) {
        shortUrlService.deleteShortUrl(shortCode);
    }

    /**
     * 短连接重定向访问
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getShortUrl(@PathVariable String shortCode) {
        String originalUrl = shortUrlService.getShortUrl(shortCode);
        // TODO 可增加其他数据校验等操作
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}