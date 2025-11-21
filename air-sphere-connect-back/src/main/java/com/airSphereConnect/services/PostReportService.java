package com.airSphereConnect.services;

import com.airSphereConnect.dtos.request.PostReportRequestDto;
import com.airSphereConnect.dtos.response.PostReportResponseDto;

import java.util.List;

public interface PostReportService {

    PostReportResponseDto createReport(PostReportRequestDto request, Long userId);

    PostReportResponseDto getReportById(Long reportId);

    List<PostReportResponseDto> getReportsByPostId(Long postId);

    List<PostReportResponseDto> getReportsByUserId(Long userId);

    List<PostReportResponseDto> getReportsByStatus(String status);

    PostReportResponseDto updateReportStatus(Long reportId, String newStatus, Long adminId);

    void deleteReport(Long reportId, Long userId);

    boolean hasUserReportedPost(Long postId, Long userId);
}

