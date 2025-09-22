package com.airSphereConnect.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ForumPostRequestDto {

    @Size(min = 2, max = 255, message = "{post.content.size}")
    private String content;
    private Long userId;
    private Long threadId;


    public ForumPostRequestDto(String content, Long userId, Long threadId) {
        this.content = content;
        this.userId = userId;
        this.threadId = threadId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }
}