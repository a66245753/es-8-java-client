package com.david.dto.response;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @authar David
 * @Date 2025/3/5
 * @description
 */
@Data
public class EsSearchResult {
    private int pageIndex;
    private int pageSize;
    private long total;
    private List<ObjectNode> list;
}
