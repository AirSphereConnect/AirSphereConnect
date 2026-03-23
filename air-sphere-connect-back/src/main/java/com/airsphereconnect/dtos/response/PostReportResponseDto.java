package com.airsphereconnect.dtos.response;

import com.airsphereconnect.entities.enums.ReportReason;

import java.time.LocalDateTime;

public record PostReportResponseDto(
        Long id,
        Long postId,
        Long userId,
        ReportReason reason,
        String description,
        String status,
        LocalDateTime createdAt
) {}

