package com.airSphereConnect.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ForumPostRequestDto {

    @Size(min = 2, max = 255, message = "{post.content.size}")
    private String content;
    @NotNull(message = "{post.userId.required}")
    private String user_id;
    @NotNull(message = "{post.threadId.required}")
    private String thread_id;


    public ForumPostRequestDto() {
    }

    public ForumPostRequestDto(String content, String user_id, String thread_id) {
        this.content = content;
        this.user_id = user_id;
        this.thread_id = thread_id;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

}
