package com.airSphereConnect.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données envoyées d'un thread du forum")
public class ForumThreadResponseDto {

    @Schema(description = "Identifiant du thread de la rubrique", example = "6")
    private Long id;

    @Schema(description = "Titre du thread de la rubrique", example = "Données de la qualité de l'air de Pézenas")
    private String title;

    @Schema(description = "Identifiant de l'auteur du thread", example = "2")
    private Long userId;

    @Schema(description = "Pseudo de l'auteur du thread", example = "Cyril")
    private String username;

    @Schema(description = "Identifiant de la rubrique parente au thread", example = "1")
    private Long rubricId;

    @Schema(description = "Titre du thread", example = "Données environnementale de l'Occitanie")
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
