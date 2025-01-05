package com.melly.myjpa.crawling;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BakeryCrawlerController {

    private final BakeryCrwalerService bakeryCrwalerService;

    @GetMapping("/crawl-cafe")
    public String crawlCafeData() {
        bakeryCrwalerService.crawlCafeData();
        return "크롤링 완료";
    }
}
