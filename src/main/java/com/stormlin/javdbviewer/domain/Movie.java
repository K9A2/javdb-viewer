package com.stormlin.javdbviewer.domain;

import lombok.Data;

/**
 * @author lin-jinting
 */
@Data
public class Movie {
    String id;
    String url;
    String title;
    float score;
    int count;
    String date;
}
