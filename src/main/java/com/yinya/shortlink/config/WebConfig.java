package com.yinya.shortlink.config;

import com.yinya.shortlink.filter.ShortCodeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ShortCodeInterceptor shortCodeInterceptor;

    public WebConfig(ShortCodeInterceptor shortCodeInterceptor) {
        this.shortCodeInterceptor = shortCodeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(shortCodeInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/**", "/redirect/**", "/error", "/favicon.ico");
    }
}
