package com.david.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @authar David
 * @Date 2025/3/5
 * @description
 */
@AllArgsConstructor
@Data
public class EsResponseBody {
    private int code;
    private Object data;
    private String msg;
}
