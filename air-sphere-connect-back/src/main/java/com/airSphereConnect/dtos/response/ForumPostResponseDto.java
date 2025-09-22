package com.airSphereConnect.dtos.response;

import com.airSphereConnect.entities.enums.ReactionType;

import java.time.LocalDateTime;

public class ForumPostResponseDto {
    private Long id;
    private String content;
    private Long userId;
    private  String username;
    private Long threadId;
    private String threadTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long likeCount;
    private long dislikeCount;
    private ReactionType currentUserReaction;

    public ForumPostResponseDto() {
    }

    public ForumPostResponseDto(Long id, String content, Long userId, String username, Long threadId, String threadTitle, LocalDateTime createdAt, long likeCount, long dislikeCount, ReactionType currentUserReaction) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.threadId = threadId;
        this.threadTitle = threadTitle;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.currentUserReaction = currentUserReaction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(long dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public ReactionType getCurrentUserReaction() {
        return currentUserReaction;
    }

    public void setCurrentUserReaction(ReactionType currentUserReaction) {
        this.currentUserReaction = currentUserReaction;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getThreadTitle() {
        return threadTitle;
    }

    public void setThreadTitle(String threadTitle) {
        this.threadTitle = threadTitle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
