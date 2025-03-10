package com.david.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * （公共）区域数据
 * @TableName sys_district
 */
@TableName(value ="sys_district")
@Data
public class SysDistrict implements Serializable {
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
    private String lng;

    /**
     * 
     */
    @TableField(value = "lat")
    private String lat;

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

    /**
     * 
     */
    @TableField(value = "location")
    private String location;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}