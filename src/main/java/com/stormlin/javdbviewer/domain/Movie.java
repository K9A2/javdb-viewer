package com.stormlin.javdbviewer.domain;

import lombok.Data;

/**
 * @author lin-jinting
 */
@Data
public class Movie {
    String code;
    String url;
    String title;
    float score;
    int count;
    String date;
}
