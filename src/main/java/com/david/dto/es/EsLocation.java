package com.david.dto.es;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Es聚合sdk对象
 */
@AllArgsConstructor
@Data
public class EsLocation implements Serializable {

    /**
     * 经度
     */
    private Double lon;

    /**
     * 纬度
     */
    private Double lat;

}