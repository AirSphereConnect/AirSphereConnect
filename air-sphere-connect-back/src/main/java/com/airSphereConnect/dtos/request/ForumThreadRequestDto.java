package com.airSphereConnect.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ForumThreadRequestDto {
    @NotBlank(message = "{thread.title.required}")
    @Size(min = 2, max = 255, message = "{thread.title.size}")
    private String title;
    private Long user_id;
    private Long rubric_id;

    public ForumThreadRequestDto() {
    }

    public ForumThreadRequestDto(String title, Long user_id, Long rubric_id) {
        this.title = title;
        this.user_id = user_id;
        this.rubric_id = rubric_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getRubric_id() {
        return rubric_id;
    }

    public void setRubric_id(Long rubric_id) {
        this.rubric_id = rubric_id;
    }
}
