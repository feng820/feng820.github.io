package com.example.stocks.utils;

public class NewsItem {
    public String newsSource;
    public String newsTitle;
    public String publishedAt;
    public String articleUrl;
    public String imageUrl;

    public NewsItem(String newsSource, String newsTitle, String publishedAt, String articleUrl, String imageUrl) {
        this.newsSource = newsSource;
        this.newsTitle = newsTitle;
        this.publishedAt = publishedAt;
        this.articleUrl = articleUrl;
        this.imageUrl = imageUrl;
    }
}
