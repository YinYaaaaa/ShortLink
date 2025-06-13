package com.yinya.shortlink.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {

    private Integer page;

    private Integer size;

    /**
     * 用户ID
     * TODO 假设为通过用户体系内部获取的数据
     */
    private Long userId;

    public Integer getPage() {
        if (page == null) {
            return 1;
        }
        return page;
    }

    public Integer getSize() {
        if (size == null) {
            return 10;
        }
        return size;
    }
}
