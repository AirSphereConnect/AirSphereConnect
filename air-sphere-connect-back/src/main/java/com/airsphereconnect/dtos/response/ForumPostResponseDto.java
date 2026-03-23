package com.airsphereconnect.dtos.response;

import com.airsphereconnect.entities.enums.ReactionType;
import com.airsphereconnect.entities.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Données envoyées d'un post")
public class ForumPostResponseDto {

    @Schema(description = "Identifiant d'un post", example = "1")
    private Long id;

    @Schema(description = "Contenu d'un post", example = "Bonjour tout le monde")
    private String content;

    @Schema(description = "Identifiant de l'auteur du post", example = "55")
    private Long userId;

    private UserRole userRole;

    @Schema(description = "Pseudo de l'auteur du post", example = "Cyril")
    private String username;

    @Schema(description = "Identifiant du thread parent du post", example = "2")
    private Long threadId;

    @Schema(description = "Titre du thread parent du post", example = "Données Climatiques de Montpellier")
    private String threadTitle;

    @Schema(description = "Date de création du post", example = "2007-12-03T10:15:30:55.000000")
    private LocalDateTime createdAt;

    @Schema(description = "Date de modification du post", example = "2007-12-03T10:15:30:55.000000")
    private LocalDateTime updatedAt;

    @Schema(description = "Nombre de likes du post", example = "3")
    private long likeCount;

    @Schema(description = "Nombre de dislikes du post", example = "1")
    private long dislikeCount;

    @Schema(description = "Type de réaction sur le post de l'utilisateur en cours", example = "Post Liké")
    private ReactionType currentUserReaction;
    private Boolean isReported;

    public ForumPostResponseDto() {
    }

    @SuppressWarnings("java:S107")
    public ForumPostResponseDto(Long id, String content, Long userId, String username, UserRole userRole, Long threadId,
                                String threadTitle, LocalDateTime createdAt, long likeCount, long dislikeCount, ReactionType currentUserReaction, Boolean isReported) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.username = username;
        this.userRole = userRole;
        this.threadId = threadId;
        this.threadTitle = threadTitle;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.currentUserReaction = currentUserReaction;
        this.isReported = isReported;
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

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Boolean getReported() {
        return isReported;
    }

    public void setReported(Boolean reported) {
        isReported = reported;
    }
}
