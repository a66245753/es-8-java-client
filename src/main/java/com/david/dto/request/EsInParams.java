package com.david.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @authar David
 * @Date 2025/3/5
 * @description
 */
@Data
public class EsInParams {

    /**
     * 字段
     */
    private String field;

    /**
     * 大于等于
     */
    private List<String> valueList;

}
