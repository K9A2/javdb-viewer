package com.stormlin.javdbviewer.service;

import com.stormlin.javdbviewer.domain.Movie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lin-jinting
 */
@Component
public class CrawlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerService.class);
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " + "Chrome/114.0.0.0 Safari/537.36";

    private static final int MAX_RETRIES = 3;

    /**
     * 用于提取评分和投票人数的正则表达式
     */
    private static final Pattern SCORE_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");

    public void startCrawler() {
        LOGGER.info("crawler service in");

        // 作业控制标准 -> 结果中是否出现重复数据。出现重复数据则表明已经到了爬取极限，需要停止采集
        Map<String, Movie> resultMap = new HashMap<>(8);
        // 当前页码，不作为作业控制用途。从 0 开始方面将页码控制功能集成到循环行首
        int currentPage = 0;
        while (true) {
            // 自动跳转到下一页
            currentPage += 1;
            // 构造页面按类别索引页面的 url
            String pageUrl = String.format("https://javdb.com/tags?c10=1&c11=2023&c5=18&c9=gt-120&page=%d", currentPage);

            // 拉取页面 html 文档
            Document document = null;
            // 重试三次后如果还是有问题就放弃这个连接，并在日志上输出错误信息
            int retryCount = 0;
            LOGGER.info("fetching next page at url: {}", pageUrl);
            while (retryCount < MAX_RETRIES) {
                try {
                    document = Jsoup.connect(pageUrl).proxy("127.0.0.1", 4780).userAgent(USER_AGENT).get();

                } catch (IOException e) {
                    LOGGER.warn("error in fetching url: {}", pageUrl);
                    retryCount += 1;
                    e.printStackTrace();
                    // 在重试次数的限制内开始下一次拉取任务
                    continue;
                }
                // 已经拉取到指定页面，进入分析分析阶段
                break;
            }
            if (document == null) {
                // 重试三次都失败，放弃该页面
                LOGGER.warn("can not get url: https://javdb.com/tags?c5=18&c10=1&c11=2023&page=1");
                break;
            }

            // 提取电影元素列表
            List<Element> itemElements = document.select("div.item").stream().toList();
            boolean isDuplicated = false;
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
                Matcher m = SCORE_PATTERN.matcher(scoreString);
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
                movie.setId(id);
                movie.setUrl(url);
                movie.setTitle(title);
                movie.setScore(score);
                movie.setCount(count);
                movie.setDate(dateString);

                // 判断是否有重复数据，如果有重复数据则意味着已经拉去了所有公开提供的数据
                if (resultMap.containsKey(id)) {
                    LOGGER.warn("try to add duplicate movie to result map: {}", id);
                    // 重复数据不再添加，停止下一页采集任务
                    isDuplicated = true;
                } else {
                    resultMap.put(id, movie);
                }
            }

            if (isDuplicated) {
                // 出现重复元素，应当停止提取下一页元素
                LOGGER.warn("stop fetching next page due to duplicated movie");
                break;
            }

            // 控制到 3 秒后拉取下一页
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                LOGGER.warn("error in sleeping, cause: {}", e.toString());
                throw new RuntimeException(e);
            }
        }

        LOGGER.info("crawler service out");

        // 输出全部拉取到的内容
        LOGGER.info("fetched {} movies", resultMap.size());
        resultMap.forEach((key, movie) -> {
            LOGGER.info("id: {}, url: {}, title: {}, score: {}, count: {}, date: {}",
                    movie.getId(), movie.getUrl(), movie.getTitle(), movie.getScore(), movie.getCount(), movie.getDate());
        });


    }
}
