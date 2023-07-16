package com.stormlin.javdbviewer.constant;

import java.util.regex.Pattern;

/**
 * @author lin-jinting
 */
public class StringConstant {
    /**
     * 爬虫用的默认 user agent 设置
     */
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " + "Chrome/114.0.0.0 Safari/537.36";

    /**
     * 标签页 url 模板
     */
    public static final String TAG_PAGE_TEMPLATE = "https://javdb.com/tags?c10=1&c11=2023&c5=18&c9=gt-120&page=%d";

    /**
     * 用于提取评分和投票人数的正则表达式
     */
    public static final Pattern SCORE_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");
}
