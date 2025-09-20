package com.airSphereConnect.dtos.response;

import java.time.LocalDateTime;

public class ForumThreadResponseDto {

    private Long id;
    private String title;
    private Long userId;
    private String username;
    private Long rubricId;
    private String rubricTitle;

    public ForumThreadResponseDto() {
    }

    public ForumThreadResponseDto(Long id, String title, Long userId, String username, Long rubricId, String rubricTitle) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.username = username;
        this.rubricId = rubricId;
        this.rubricTitle = rubricTitle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getRubricId() {
        return rubricId;
    }

    public void setRubricId(Long rubricId) {
        this.rubricId = rubricId;
    }

    public String getRubricTitle() {
        return rubricTitle;
    }

    public void setRubricTitle(String rubricTitle) {
        this.rubricTitle = rubricTitle;
    }

}
