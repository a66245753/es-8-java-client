package com.david.dto.es;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Es聚合sdk对象
 */
@Data
public class EsDocAddBatch implements Serializable {

    /**
     * 索引
     */
    private String index;

    /**
     * 数据
     */
    private List<ObjectNode> docs;

}