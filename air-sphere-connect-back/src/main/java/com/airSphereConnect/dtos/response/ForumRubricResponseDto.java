package com.airSphereConnect.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données envoyées d'une rubrique du forum")
public class ForumRubricResponseDto {

    @Schema(description = "Identifiant de la rubrique du forum", example = "2")
    private Long id;

    @Schema(description = "Titre de la rubrique du forum", example = "Données climatique")
    private String title;

    @Schema(description = "Description de la rubrique du forum", example = "Données climatique en temps réel ")
    private String description;

    @Schema(description = "Identifiant de l'auteur de la rubrique", example = "1")
    private Long userId;

    @Schema(description = "Pseudo de l'auteur de la rubrique", example = "Cyril")
    private String username;

    @Schema(description = "Identifiant du forum", example = "1")
    private Long forumId;

    @Schema(description = "Titre du forum", example = "Forum sur l'environnement")
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
