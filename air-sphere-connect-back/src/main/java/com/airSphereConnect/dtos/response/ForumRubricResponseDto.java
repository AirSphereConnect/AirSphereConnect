package com.airSphereConnect.dtos.response;

import java.time.LocalDateTime;

public class ForumRubricResponseDto {

    private Long id;
    private String title;
    private String description;
    private Long userId;
    private String username;
    private Long forumId;
    private String forumTitle;

    public ForumRubricResponseDto() {
    }

    public ForumRubricResponseDto(Long id, String title, String description, Long userId, String username, Long forumId, String forumTitle) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.username = username;
        this.forumId = forumId;
        this.forumTitle = forumTitle;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getForumId() {
        return forumId;
    }

    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }

    public String getForumTitle() {
        return forumTitle;
    }

    public void setForumTitle(String forumTitle) {
        this.forumTitle = forumTitle;
    }
}
