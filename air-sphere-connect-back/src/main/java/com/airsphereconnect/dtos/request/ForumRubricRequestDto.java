package com.airsphereconnect.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Données reçues d'une rubrique du forum")
public class ForumRubricRequestDto {

    @Schema(description = "Titre d'une rubrique du forum", example = "Qualité de l'air")
    @NotBlank(message = "{rubric.title.required}")
    @Size(min = 2, max = 255, message = "{rubric.title.size}")
    private String title;

    @Schema(description = "Description d'une rubrique du forum", example = "Qualité de l'air à Montpellier")
    @NotBlank(message = "{rubric.description.required}")
    @Size(min = 2, max = 255, message = "{rubric.description.size}")
    private String description;

    @Schema(description = "Identifiant de l'utilisateur qui créé la rubrique", example = "1")
    private Long userId;

    @Schema(description = "Identifiant du forum dans le quel se trouve la rubrique", example = "1")
    private Long forumId;

    public ForumRubricRequestDto() {
    }

    public ForumRubricRequestDto(String title, String description, Long userId, Long forumId) {
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.forumId = forumId;
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

    public Long getForumId() {
        return forumId;
    }

    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }
}
