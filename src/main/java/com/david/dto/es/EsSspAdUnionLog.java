package com.david.dto.es;

import lombok.Data;

import java.io.Serializable;

/**
 * Es聚合sdk对象
 */
@Data
public class EsSspAdUnionLog implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     *
     */
    private String reqId;

    /**
     *
     */
    private String device;

    /**
     * 平台
     */
    private Integer platform;

    /**
     * 平台
     */
    private String platformName;

    /**
     * 1 安卓 2 ios
     */
    private Integer clientType;

    /**
     * 媒体appid
     */
    private String myAppId;

    /**
     * 设备号
     */
    private String deviceId;

    /**
     * 广告位id
     */
    private Long adSiteGroupId;

    /**
     * 代码位id
     */
    private String adSiteId;

    /**
     * 媒体包名
     */
    private String packagePath;

    /**
     * 竞胜ecpm
     */
    private Integer ecpm;

    /**
     * 经度
     */
    private EsLocation location;

    /**
     * ip
     */
    private String ip;

    /**
     * 市
     */
    private Integer cityId;

    /**
     * 区
     */
    private Integer areaId;

    /**
     * 市
     */
    private String cityName;

    /**
     * 区
     */
    private String areaName;

    /**
     * 省
     */
    private Integer provinceId;

    /**
     * 省
     */
    private String provinceName;

    /**
     * 手机品牌
     */
    private String phoneBrand;

    /**
     * 手机品牌
     */
    private String phoneBrandName;

    /**
     * 手机型号
     */
    private String phoneModel;

    /**
     * ios 授权设备标识
     */
    private String idfa;

    /**
     * 1 请求  2 竞价 3 展示  4 点击
     */
    private Integer bizType;

    /**
     * sdk 版本
     */
    private String sdkVersion;

    /**
     * wifi/5g/4g/其他
     */
    private String network;

    /**
     * 实际请求时间
     */
    private String logTime;

    /**
     *
     */
    private String createdAt;

}