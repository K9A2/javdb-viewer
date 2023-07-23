package com.stormlin.javdbviewer.service;

import com.stormlin.javdbviewer.constant.StringConstant;
import com.stormlin.javdbviewer.domain.Movie;
import com.stormlin.javdbviewer.utils.ParsingUtil;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lin-jinting
 */
@Component
public class CrawlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerService.class);

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
            String pageUrl = String.format(StringConstant.TAG_PAGE_TEMPLATE, currentPage);

            // 拉取页面 html 文档
            LOGGER.info("fetching next page at url: {}", pageUrl);
            Document document = ParsingUtil.getDocument(pageUrl);
            if (document == null) {
                // 无法获取到当前资源，重试三次后放弃
                LOGGER.warn("error in fetching url: {}", pageUrl);
                continue;
            }

            // 从标签页中提取电影列表
            List<Movie> movies = ParsingUtil.parseTagPageResult(document);
            // 判断是否有重复数据，如果有重复数据则意味着已经拉去了所有公开提供的数据，不需要再提取下一页
            boolean isDuplicated = false;
            for (Movie m : movies) {
                String code = m.getCode();
                if (resultMap.containsKey(m.getCode())) {
                    LOGGER.warn("try to add duplicate movie to result map: {}", code);
                    isDuplicated = true;
                } else {
                    resultMap.put(code, m);
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
        resultMap.forEach((key, movie) -> LOGGER.info("code: {}, url: {}, title: {}, score: {}, count: {}, date: {}",
                movie.getCode(), movie.getUrl(), movie.getTitle(), movie.getScore(), movie.getCount(), movie.getDate()));
    }
}
