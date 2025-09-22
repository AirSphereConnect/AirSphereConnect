package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.ForumResponseDto;
import com.airSphereConnect.dtos.response.ForumRubricResponseDto;
import com.airSphereConnect.services.ForumRubricService;
import com.airSphereConnect.services.ForumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/forums")
public class ForumController {
    private final ForumService forumService;
    private final ForumRubricService forumRubricService;

    public ForumController(ForumService forumService, ForumRubricService forumRubricService) {
        this.forumService = forumService;
        this.forumRubricService = forumRubricService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForumResponseDto> getForumById(@PathVariable Long id) {
        ForumResponseDto response = forumService.getForumById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/rubrics")
    public ResponseEntity<List<ForumRubricResponseDto>> getRubricsByForumId(@PathVariable Long id) {
        List<ForumRubricResponseDto> rubrics = forumRubricService.getRubricsByForumId(id);
        return ResponseEntity.ok(rubrics);
    }




}


