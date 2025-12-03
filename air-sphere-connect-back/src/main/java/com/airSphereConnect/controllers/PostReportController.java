package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.PostReportRequestDto;
import com.airSphereConnect.dtos.response.PostReportResponseDto;
import com.airSphereConnect.services.PostReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post-reports")
public class PostReportController {

    private final PostReportService postReportService;

    public PostReportController(PostReportService postReportService) {
        this.postReportService = postReportService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/new/{userId}")
    public ResponseEntity<PostReportResponseDto> createReport(
            @Valid @RequestBody PostReportRequestDto request,
            @PathVariable Long userId) {
        PostReportResponseDto response = postReportService.createReport(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PostReportResponseDto> getReportById(@PathVariable Long id) {
        PostReportResponseDto response = postReportService.getReportById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostReportResponseDto>> getReportsByPostId(@PathVariable Long postId) {
        List<PostReportResponseDto> reports = postReportService.getReportsByPostId(postId);
        return ResponseEntity.ok(reports);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostReportResponseDto>> getReportsByUserId(@PathVariable Long userId) {
        List<PostReportResponseDto> reports = postReportService.getReportsByUserId(userId);
        return ResponseEntity.ok(reports);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PostReportResponseDto>> getReportsByStatus(
            @RequestParam(required = false, defaultValue = "PENDING") String status) {
        List<PostReportResponseDto> reports = postReportService.getReportsByStatus(status);
        return ResponseEntity.ok(reports);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<PostReportResponseDto> updateReportStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam Long adminId) {
        PostReportResponseDto response = postReportService.updateReportStatus(id, status, adminId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(
            @PathVariable Long id,
            @RequestParam Long userId) {
        postReportService.deleteReport(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/check")
    public ResponseEntity<Boolean> hasUserReportedPost(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        boolean hasReported = postReportService.hasUserReportedPost(postId, userId);
        return ResponseEntity.ok(hasReported);
    }
}

