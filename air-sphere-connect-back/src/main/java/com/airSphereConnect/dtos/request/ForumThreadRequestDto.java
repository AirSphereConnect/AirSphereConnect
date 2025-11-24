package com.airSphereConnect.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Données reçues d'un thread du forum")
public class ForumThreadRequestDto {

    @Schema(description = "Titre du thread", example = "Données climatiques sur le littoral")
    @NotBlank(message = "{thread.title.required}")
    @Size(min = 2, max = 255, message = "{thread.title.size}")
    private String title;

    @Schema(description = "Identifiant de l'utilisateur créateur du thread", example = "1")
    private Long userId;

    @Schema(description = "Identifiant de la rubrique mère du thread", example = "2")
    private Long rubricId;

    public ForumThreadRequestDto() {

    }

    public ForumThreadRequestDto(String title, Long userId, Long rubricId) {
        this.title = title;

        this.userId = userId;
        this.rubricId = rubricId;
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

    public Long getRubricId() {
        return rubricId;
    }

    public void setRubricId(Long rubricId) {
        this.rubricId = rubricId;
    }
}
