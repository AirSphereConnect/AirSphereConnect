package com.airsphereconnect.dtos.request;

import com.airsphereconnect.entities.enums.ReportReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostReportRequestDto(
        @NotNull(message = "{postReport.postId.required}") Long postId,
        @NotNull(message = "{postReport.reason.required}") ReportReason reason,
        @Size(max = 500, message = "{postReport.description.size}") String description
) {}

