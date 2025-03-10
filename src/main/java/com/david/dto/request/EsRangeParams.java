package com.david.dto.request;

import lombok.Data;

/**
 * @authar David
 * @Date 2025/3/5
 * @description
 */
@Data
public class EsRangeParams {

    /**
     * 字段
     */
    private String field;

    /**
     * 数据类型：1数值 2时间
     */
    private int fieldType = 1;

    /**
     * 大于等于
     */
    private String gte;

    /**
     * 小于等于
     */
    private String lte;

}
