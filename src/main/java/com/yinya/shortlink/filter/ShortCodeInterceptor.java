package com.yinya.shortlink.filter;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.yinya.shortlink.service.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ShortCodeInterceptor implements HandlerInterceptor {

    private final ShortUrlService shortUrlService;

    public ShortCodeInterceptor(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        if (path.startsWith("/api/") || path.contains(".") || path.startsWith("/redirect")) {
            return true;
        }

        String shortCode = path.startsWith("/") ? path.substring(1) : path;

        String shortUrl = shortUrlService.getShortUrl(shortCode);
        if (ObjectUtils.isNotEmpty(shortUrl)) {
            // ①直接重定向至原始链接
            response.sendRedirect(shortUrl);
            // ②若需要记录其他信息，可先重定向至中间层接口
            // response.sendRedirect("/redirect/" + shortCode);
            return false;
        }

        return true;
    }
}
