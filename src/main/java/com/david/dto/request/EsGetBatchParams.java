package com.david.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @authar David
 * @Date 2025/3/5
 * @description
 */
@Data
public class EsGetBatchParams {

    /**
     * 索引
     */
    private String index;

    /**
     * id
     */
    private List<String> idList;

}
