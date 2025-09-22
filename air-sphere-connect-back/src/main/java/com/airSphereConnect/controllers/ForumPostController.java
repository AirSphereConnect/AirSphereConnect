package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.ForumPostRequestDto;
import com.airSphereConnect.dtos.response.ForumPostResponseDto;
import com.airSphereConnect.entities.enums.ReactionType;
import com.airSphereConnect.services.ForumPostService;
import com.airSphereConnect.services.PostReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum-posts")
public class ForumPostController {

    private final ForumPostService forumPostService;
    private final PostReactionService postReactionService;

    public ForumPostController(ForumPostService forumPostService, PostReactionService postReactionService) {
        this.forumPostService = forumPostService;
        this.postReactionService = postReactionService;
    }


    @GetMapping
    public ResponseEntity<List<ForumPostResponseDto>> getAllActivePosts(
            @RequestParam(required = false) Long currentUserId) {

        List<ForumPostResponseDto> posts = forumPostService.getAllActivePosts();
        enrichWithReactions(posts, currentUserId);
        return ResponseEntity.ok(posts);
    }

    private void enrichWithReactions(List<ForumPostResponseDto> posts, Long currentUserId) {
        for (ForumPostResponseDto post : posts) {
            enrichWithReactions(post, currentUserId);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForumPostResponseDto> getPostWithReactions(
            @PathVariable Long id,
            @RequestParam(required = false) Long currentUserId) {

        ForumPostResponseDto post = forumPostService.getPostById(id);
        enrichWithReactions(post, currentUserId);
        return ResponseEntity.ok(post);
    }


    private void enrichWithReactions(ForumPostResponseDto post, Long currentUserId) {
        post.setLikeCount(postReactionService.countLikesByPost(post.getId()));
        post.setDislikeCount(postReactionService.countDislikesByPost(post.getId()));

        if (currentUserId != null) {
            ReactionType userReaction = postReactionService.getUserReaction(post.getId(), currentUserId);
            post.setCurrentUserReaction(userReaction);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ForumPostResponseDto>> getPostsByUser(@PathVariable Long userId) {
        List<ForumPostResponseDto> responses = forumPostService.getPostsByUserId(userId);
        return ResponseEntity.ok(responses);
    }


    @PostMapping("/new/{userId}")
    public ResponseEntity<ForumPostResponseDto> createPost(
            @Valid @RequestBody ForumPostRequestDto request,
            @PathVariable Long userId) {

        ForumPostResponseDto response = forumPostService.createPost(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PostMapping("/{postId}/reaction")
    public ResponseEntity<ForumPostResponseDto> toggleReaction(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestParam ReactionType reaction) {

        postReactionService.toggleReaction(postId, userId, reaction);
        return getPostWithReactions(postId, userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ForumPostResponseDto> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody ForumPostRequestDto request,
            @RequestParam Long userId) {

        ForumPostResponseDto response = forumPostService.updatePost(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @RequestParam Long userId) {

        forumPostService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }
}
