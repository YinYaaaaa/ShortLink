package com.yinya.shortlink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yinya.shortlink.model.ShortUrl;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShortUrlMapper extends BaseMapper<ShortUrl> {
}
