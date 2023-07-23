package com.stormlin.javdbviewer.domain;

import lombok.Data;

/**
 * @author lin-jinting
 */
@Data
public class Movie {
    /**
     * 番号
     */
    String code;
    /**
     * 页面地址
     */
    String url;
    /**
     * 片名
     */
    String title;
    /**
     * 得分
     */
    float score;
    /**
     * 投票人数
     */
    int count;
    /**
     * 日期
     */
    String date;
    /**
     * 看过人数
     */
    int seen;
    /**
     * 想看人数
     */
    int want;
}
