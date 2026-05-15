package com.crewise.backend.domain.news.dto;

import com.crewise.backend.domain.news.entity.News;
import lombok.Getter;

@Getter
public class NewsResponse {

    private Long newsId;
    private String targetType;
    private Long targetId;
    private String newsContent;
    private String teamId;

    public static NewsResponse from(News news) {
        NewsResponse response = new NewsResponse();
        response.newsId = news.getNewsId();
        response.targetType = news.getTargetType();
        response.targetId = news.getTargetId();
        response.newsContent = news.getNewsContent();
        response.teamId = news.getTeamId();
        return response;
    }
}