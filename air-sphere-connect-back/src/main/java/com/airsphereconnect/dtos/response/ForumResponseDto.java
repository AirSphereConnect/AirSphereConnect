package com.airsphereconnect.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données concernant le forum")
public class ForumResponseDto {

    @Schema(description = "Identifiant du forum", example = "1")
    private Long id;

    @Schema(description = "Titre du forum", example = "Forum sur l'écologie")
    private String title;

    @Schema(description = "Description du forum", example = "Ici vous trouverez toutes les discussions disponibles " +
            "sur l'environnement")
    private String description;

    public ForumResponseDto() {
    }

    public ForumResponseDto(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
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
}
