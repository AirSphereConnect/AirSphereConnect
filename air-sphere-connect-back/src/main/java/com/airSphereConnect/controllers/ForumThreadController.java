package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.ForumThreadRequestDto;
import com.airSphereConnect.dtos.response.ForumThreadResponseDto;
import com.airSphereConnect.services.ForumThreadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
@RequestMapping("/forum-threads")
public class ForumThreadController {
    private final ForumThreadService forumThreadService;

    public ForumThreadController(ForumThreadService forumThreadService) {
        this.forumThreadService = forumThreadService;
    }

    @GetMapping
    public ResponseEntity<List<ForumThreadResponseDto>> getAllActiveThreads() {
        List<ForumThreadResponseDto> responses = forumThreadService.getAllActiveThreads();
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ForumThreadResponseDto> getThreadById(@PathVariable Long id) {
        ForumThreadResponseDto response = forumThreadService.getThreadById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ForumThreadResponseDto>> getThreadsByUser(@PathVariable Long userId) {
        List<ForumThreadResponseDto> responses = forumThreadService.getThreadsByCurrentUser(userId);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/new/{userId}")
    public ResponseEntity<ForumThreadResponseDto> createThread(
            @Valid @RequestBody ForumThreadRequestDto request,
            @PathVariable Long userId) {

        ForumThreadResponseDto response = forumThreadService.createThread(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ForumThreadResponseDto> updateThread(
            @PathVariable Long id,
            @RequestBody ForumThreadRequestDto request,
            @RequestParam Long userId) {

        ForumThreadResponseDto response = forumThreadService.updateThread(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThread(
            @PathVariable Long id,
            @RequestParam Long userId) {

        forumThreadService.deleteThread(id, userId);
        return ResponseEntity.noContent().build();
    }
}
