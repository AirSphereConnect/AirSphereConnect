package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.ForumRubricRequestDto;
import com.airSphereConnect.dtos.response.ForumRubricResponseDto;
import com.airSphereConnect.services.ForumRubricService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
@RequestMapping("/api/forum-rubrics")
public class ForumRubricController {
    private final ForumRubricService forumRubricService;

    public ForumRubricController(ForumRubricService forumRubricService) {
        this.forumRubricService = forumRubricService;
    }

    @GetMapping
    public ResponseEntity<List<ForumRubricResponseDto>> getAllActiveRubrics() {
        List<ForumRubricResponseDto> responses = forumRubricService.getAllActiveRubrics();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForumRubricResponseDto> getRubricById(@PathVariable Long id) {
        ForumRubricResponseDto response = forumRubricService.getRubricById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ForumRubricResponseDto>> getRubricsByUser(@PathVariable Long userId) {
        List<ForumRubricResponseDto> responses = forumRubricService.getRubricsByCurrentUser(userId);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/new/{userId}")
    public ResponseEntity<ForumRubricResponseDto> createRubric(
            @Valid @RequestBody ForumRubricRequestDto request,
            @PathVariable Long userId) {

        ForumRubricResponseDto response = forumRubricService.createRubric(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ForumRubricResponseDto> updateRubric(
            @PathVariable Long id,
            @Valid @RequestBody ForumRubricRequestDto request,
            @RequestParam Long userId) {

        ForumRubricResponseDto response = forumRubricService.updateRubric(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRubric(
            @PathVariable Long id,
            @RequestParam Long userId) {

        forumRubricService.deleteRubric(id, userId);
        return ResponseEntity.noContent().build();
    }
}
