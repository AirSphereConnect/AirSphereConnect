package com.airSphereConnect.entities;

import com.airSphereConnect.entities.enums.ReactionType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_reactions")
public class PostReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private ForumPost post;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    // Constructeurs
    public PostReaction() {}

    public PostReaction(User user, ForumPost post, ReactionType reactionType) {
        this.user = user;
        this.post = post;
        this.reactionType = reactionType;
    }