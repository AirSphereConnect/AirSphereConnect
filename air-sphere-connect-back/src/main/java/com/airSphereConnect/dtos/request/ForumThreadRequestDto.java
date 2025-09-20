package com.airSphereConnect.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ForumThreadRequestDto {
    @NotBlank(message = "{thread.title.required}")
    @Size(min = 2, max = 255, message = "{thread.title.size}")
    private String title;
    private Long userId;
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
