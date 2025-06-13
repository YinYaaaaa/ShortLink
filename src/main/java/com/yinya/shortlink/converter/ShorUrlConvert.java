package com.yinya.shortlink.converter;

import com.yinya.shortlink.model.ShortUrl;
import com.yinya.shortlink.response.ShortUrlResponse;

public class ShorUrlConvert {

    public static ShortUrlResponse bo2vo(ShortUrl shortUrl) {
        return new ShortUrlResponse()
                .setOriginalUrl(shortUrl.getOriginalUrl())
                .setShortUrl(shortUrl.getShortCode())
                .setExpiresAt(shortUrl.getExpiresAt())
                .setCreatedAt(shortUrl.getCreatedAt());
    }
}
