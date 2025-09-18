package com.airSphereConnect.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ForumRubricRequestDto {
    @NotBlank(message = "{rubric.title.required}")
    @Size(min = 2, max = 255, message = "{rubric.title.size}")
    private String title;
    @NotBlank(message = "{rubric.description.required}")
    @Size(min = 2, max = 255, message = "{rubric.description.size}")
    private String description;

    public ForumRubricRequestDto() {
    }

    public ForumRubricRequestDto(String title, String description) {
        this.title = title;
        this.description = description;
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
