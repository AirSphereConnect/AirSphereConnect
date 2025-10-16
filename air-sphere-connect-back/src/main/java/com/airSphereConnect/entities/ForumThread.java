package com.airSphereConnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "forum_threads")
public class ForumThread extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{forumThread.title.required}")
    @Size(max = 255, message = "{forumThread.title.size}")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @NotNull(message = "{forumThread.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "{forumThread.forumRubric.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rubric_id", nullable = false)
    private ForumRubric forumRubric;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ForumPost> forumPosts = new ArrayList<>();

    public ForumThread() {
    }

    public ForumThread(String title, User user, ForumRubric forumRubric) {
        this.title = title;
        this.user = user;
        this.forumRubric = forumRubric;
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


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ForumRubric getForumRubric() {
        return forumRubric;
    }

    public void setForumRubric(ForumRubric forumRubric) {
        this.forumRubric = forumRubric;
    }

    public List<ForumPost> getForumPosts() {
        return forumPosts;
    }

    public void setForumPosts(List<ForumPost> forumPosts) {
        this.forumPosts = forumPosts;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ForumThread that = (ForumThread) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ForumThread{" +
                "title='" + title + '\'' +
                '}';
    }
}
