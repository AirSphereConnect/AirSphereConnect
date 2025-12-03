package com.airSphereConnect.entities;

import com.airSphereConnect.entities.enums.ReportReason;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity
@Table(name = "post_reports")
public class PostReport extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{postReport.user.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "{postReport.post.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private ForumPost post;

    @NotNull(message = "{postReport.reason.required}")
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private ReportReason reason;

    @Size(max = 500, message = "{postReport.description.size}")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, REVIEWED, DISMISSED, ACTION_TAKEN

    public PostReport() {
    }

    public PostReport(User user, ForumPost post, ReportReason reason) {
        this.user = user;
        this.post = post;
        this.reason = reason;
    }

    public PostReport(User user, ForumPost post, ReportReason reason, String description) {
        this.user = user;
        this.post = post;
        this.reason = reason;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ForumPost getPost() {
        return post;
    }

    public void setPost(ForumPost post) {
        this.post = post;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PostReport that = (PostReport) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PostReport{" +
                "id=" + id +
                ", user=" + user +
                ", post=" + post +
                ", reason=" + reason +
                ", status='" + status + '\'' +
                '}';
    }
}

