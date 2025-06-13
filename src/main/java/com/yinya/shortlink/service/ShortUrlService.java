package com.yinya.shortlink.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yinya.shortlink.request.PageQuery;
import com.yinya.shortlink.request.ShortUrlRequest;
import com.yinya.shortlink.response.ShortUrlResponse;

public interface ShortUrlService {

    /**
     * 生成短连接
     */
    ShortUrlResponse createShortUrl(ShortUrlRequest request);

    /**
     * 通过短码获取原链接
     */
    String getShortUrl(String shortCode);

    /**
     * 删除短链接
     */
    void deleteShortUrl(String shortCode);

    Page<ShortUrlResponse> getShortUrls(PageQuery request);
}
