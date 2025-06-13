package com.yinya.shortlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yinya.shortlink.converter.ShorUrlConvert;
import com.yinya.shortlink.exception.ShortCodeAlreadyExistsException;
import com.yinya.shortlink.exception.ShortUrlNotFoundException;
import com.yinya.shortlink.mapper.ShortUrlMapper;
import com.yinya.shortlink.model.ShortUrl;
import com.yinya.shortlink.request.PageQuery;
import com.yinya.shortlink.request.ShortUrlRequest;
import com.yinya.shortlink.response.ShortUrlResponse;
import com.yinya.shortlink.service.ShortUrlService;
import com.yinya.shortlink.util.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ShortUrlServiceImpl extends ServiceImpl<ShortUrlMapper, ShortUrl> implements ShortUrlService {

    private final ShortCodeGenerator codeGenerator;
    private final ShortUrlMapper shortUrlMapper;

    private final RedisTemplate<String, String> redisTemplate;


    @Value("${shorturl.domain}")
    private String domain;

    @Override
    @Transactional
    public ShortUrlResponse createShortUrl(ShortUrlRequest request) {
        if (ObjectUtils.isNotEmpty(request.getCustomShortCode())) {
            VerifyCode(request);
        } else {
            request.setCustomShortCode(generateUniqueCode());
        }

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setUserId(request.getUserId());
        shortUrl.setOriginalUrl(request.getOriginalUrl());
        shortUrl.setShortCode(request.getCustomShortCode());
        shortUrl.setExpiresAt(request.getExpiresAt());

        shortUrlMapper.insert(shortUrl);

        String cacheKey = "shorturl:" + request.getCustomShortCode();
        redisTemplate.opsForValue().set(cacheKey, shortUrl.getId().toString(),
                getTtlInSeconds(shortUrl.getExpiresAt()), TimeUnit.SECONDS);

        return buildResponse(shortUrl);
    }

    @Override
    public String getShortUrl(String shortCode) {
        String cacheKey = "shorturl:" + shortCode;
        String cachedId = redisTemplate.opsForValue().get(cacheKey);

        ShortUrl shortUrl;
        if (cachedId != null) {
            shortUrl = shortUrlMapper.selectById(Long.valueOf(cachedId));
        } else {
            shortUrl = getByCode(shortCode);
            if (ObjectUtils.isEmpty(shortUrl)) {
                throw new ShortUrlNotFoundException("未找到对应的短链接[%s]".formatted(shortCode));
            }
            redisTemplate.opsForValue().set(cacheKey, shortUrl.getId().toString(),
                    getTtlInSeconds(shortUrl.getExpiresAt()), TimeUnit.SECONDS);
        }

        return shortUrl.getOriginalUrl();
    }

    @Override
    public void deleteShortUrl(String shortCode) {
        ShortUrl shortUrl = getByCode(shortCode);

        /*
        todo 校验用户权限
        if (shortUrl.getUserId() != 1L){
            throw new RuntimeException("无权限删除该短链接");
        }
        */
        if (ObjectUtils.isEmpty(shortUrl)) {
            throw new ShortUrlNotFoundException("未找到对应的短链接[%s]，请重试".formatted(shortCode));
        }

        shortUrlMapper.deleteById(shortUrl.getId());

        String cacheKey = "shorturl:" + shortCode;
        redisTemplate.delete(cacheKey);
    }

    @Override
    public Page<ShortUrlResponse> getShortUrls(PageQuery request) {
        Page<ShortUrl> page = shortUrlMapper.selectPage(new Page<>(request.getPage(), request.getSize(), true),
                new LambdaQueryWrapper<ShortUrl>().eq(ShortUrl::getUserId, request.getUserId()));
        if (ObjectUtils.isEmpty(page.getRecords())) {
            return new Page<>();
        }
        List<ShortUrlResponse> vo = page.getRecords().stream().map(ShorUrlConvert::bo2vo).toList();
        return new Page<ShortUrlResponse>()
                .setTotal(page.getTotal())
                .setRecords(vo);
    }

    /**
     * 生成短码
     */
    private String generateUniqueCode() {
        String shortCode;
        do {
            shortCode = codeGenerator.generateUniqueCode();
        } while (ObjectUtils.isNotEmpty(getByCode(shortCode)));
        return shortCode;
    }

    /**
     * 验证短码是否存在
     */
    private void VerifyCode(ShortUrlRequest request) {
        if (ObjectUtils.isNotEmpty(getByCode(request.getCustomShortCode()))) {
            throw new ShortCodeAlreadyExistsException("已存在相同短码[%s]，请更换后重试".formatted(request.getCustomShortCode()));
        }
    }

    private ShortUrl getByCode(String shortCode) {
        return shortUrlMapper.selectOne(new LambdaQueryWrapper<ShortUrl>()
                .eq(ShortUrl::getShortCode, shortCode)
                .ge(ShortUrl::getExpiresAt, LocalDateTime.now()));
    }

    private ShortUrlResponse buildResponse(ShortUrl shortUrl) {
        return ShortUrlResponse.builder()
                .originalUrl(shortUrl.getOriginalUrl())
                .shortUrl(domain + "/" + shortUrl.getShortCode())
                .expiresAt(shortUrl.getExpiresAt())
                .build();
    }

    private long getTtlInSeconds(LocalDateTime expireTime) {
        return java.time.Duration.between(LocalDateTime.now(), expireTime).getSeconds();
    }
}