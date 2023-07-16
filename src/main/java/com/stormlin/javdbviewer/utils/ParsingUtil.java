package com.stormlin.javdbviewer.utils;

import com.stormlin.javdbviewer.constant.StringConstant;
import com.stormlin.javdbviewer.domain.Movie;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author lin-jinting
 */
public class ParsingUtil {
    public static List<Movie> parseTagPageResult(Document document) {
        List<Movie> result = new ArrayList<>();
        // 提取电影元素列表
        List<Element> itemElements = document.select("div.item").stream().toList();
        for (Element item : itemElements) {
            // 提取 url 连接
            Elements urlElement = item.select("a");
            String url = urlElement.get(0).attr("href");
            // 提取电影番号和标题
            Elements titleElement = item.select("div.video-title");
            String id = titleElement.get(0).select("strong").get(0).text();
            String title = titleElement.get(0).childNodes().get(1).toString().trim();
            // 提取评分和投票人数
            Elements scoreElement = item.select("div.score");
            String scoreString = scoreElement.get(0).text();
            Matcher m = StringConstant.SCORE_PATTERN.matcher(scoreString);
            List<String> found = new ArrayList<>();
            while (m.find()) {
                found.add(m.group());
            }
            float score = Float.parseFloat(found.get(0));
            int count = Integer.parseInt(found.get(1));
            // 提取日期
            Elements dateElement = item.select("div.meta");
            String dateString = dateElement.get(0).text();

            // 构造结构体
            Movie movie = new Movie();
            movie.setCode(id);
            movie.setUrl(url);
            movie.setTitle(title);
            movie.setScore(score);
            movie.setCount(count);
            movie.setDate(dateString);
            result.add(movie);
        }
        return result;
    }
}
