package com.stormlin.javdbviewer.controller;

import com.stormlin.javdbviewer.service.CrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lin-jinting
 */
@RestController
@RequestMapping("/crawler")
public class CrawlerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerController.class);

    @Autowired
    private CrawlerService crawlerService;

    @GetMapping("/startCrawler")
    public String startCrawler() {
        LOGGER.info("crawler start");
        crawlerService.startCrawler();
        return "crawler start";
    }
}
