package com.david.dto.request;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Es聚合sdk对象
 */
@AllArgsConstructor
@Data
public class EsDocBucketError implements Serializable {

    /**
     * 错误原因
     */
    private String errorReason;

    /**
     * 错误数据
     */
    private ObjectNode doc;

}