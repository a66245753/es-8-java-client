package com.david.dto.es;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * （公共）区域数据
 * @TableName sys_district
 */
@Data
public class EsSysDistrict {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父栏目
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 
     */
    @TableField(value = "name")
    private String name;

    /**
     * 
     */
    @TableField(value = "zipcode")
    private String zipcode;

    /**
     * 
     */
    @TableField(value = "pinyin")
    private String pinyin;

    /**
     * 
     */
    @TableField(value = "lng")
    private EsLocation location;

    /**
     * 
     */
    @TableField(value = "level")
    private Integer level;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

}