package com.realthon.etf.recommendation.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;

@Component
public class TitleExtractor {

    public String extractTitle(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (compatible; ETFBot/1.0; +https://example.com)")
                    .timeout((int) Duration.ofSeconds(3).toMillis())
                    .followRedirects(true)
                    .get();

            String ogTitle = doc.select("meta[property=og:title]").attr("content");
            if (ogTitle != null && !ogTitle.isBlank()) return ogTitle.trim();

            String twitterTitle = doc.select("meta[name=twitter:title]").attr("content");
            if (twitterTitle != null && !twitterTitle.isBlank()) return twitterTitle.trim();

            String title = doc.title();
            if (title != null && !title.isBlank()) return title.trim();

        } catch (Exception ignored) {
            // 실패 시 fallback
        }
        return fallbackTitle(url);
    }

    private String fallbackTitle(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            return (host != null && !host.isBlank()) ? host.replaceFirst("^www\\.", "") : "추천 링크";
        } catch (Exception e) {
            return "추천 링크";
        }
    }
}