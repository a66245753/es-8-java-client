package com.david.dto.es;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Es聚合sdk对象
 */
@Data
public class EsDocAdd implements Serializable {

    /**
     * 索引
     */
    private String index;

    /**
     * 数据
     */
    private ObjectNode doc;

}