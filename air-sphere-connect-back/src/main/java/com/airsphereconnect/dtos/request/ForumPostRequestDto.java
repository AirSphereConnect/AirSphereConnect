package com.airsphereconnect.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Donnée reçues d'un post du forum")
public class ForumPostRequestDto {

    @Schema(description = "Contenu d'un post", example = "Bonjour tout le monde")
    @Size(min = 2, max = 255, message = "{post.content.size}")
    private String content;
    @Schema(description = "Identifiant de l'utilisateur qui écrit le post", example = "1")
    private Long userId;
    @Schema(description = "Identifiant du thread parent du post", example = "2")
    private Long threadId;

    public ForumPostRequestDto() {
    }


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