package com.airSphereConnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "forum")
public class Forum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{forum.title.required}")
    @Size(max = 200, message = "{forum.title.size}")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Size(max = 2000, message = "{forum.description.size}")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ForumRubric> forumRubrics = new ArrayList<>();

    public Forum() {
    }

    public Forum(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Forum(String title, String description, List<ForumRubric> forumRubrics) {
        this.title = title;
        this.description = description;
        this.forumRubrics = forumRubrics;
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

    public List<ForumRubric> getForumRubrics() {
        return forumRubrics;
    }

    public void setForumRubrics(List<ForumRubric> forumRubrics) {
        this.forumRubrics = forumRubrics;
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Forum forum = (Forum) o;
        return Objects.equals(id, forum.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Forum{}";
    }
}
