package com.crewise.backend.domain.news.dto;

import com.crewise.backend.domain.news.entity.Comment;
import lombok.Getter;

@Getter
public class CommentResponse {

    private Long cmtId;
    private String cmtContent;
    private String cmtRegDtm;
    private String cmtModDtm;
    private Long newsId;
    private String memId;

    private String memNic;
    private String userImg;

    public static CommentResponse from(Comment comment) {
        return from(comment, null, null);
    }

    public static CommentResponse from(Comment comment, String memNic, String userImg) {
        CommentResponse response = new CommentResponse();
        response.cmtId = comment.getCmtId();
        response.cmtContent = comment.getCmtContent();
        response.cmtRegDtm = comment.getCmtRegDtm();
        response.cmtModDtm = comment.getCmtModDtm();
        response.newsId = comment.getNewsId();
        response.memId = comment.getMemId();
        response.memNic = memNic;
        response.userImg = userImg;
        return response;
    }
}