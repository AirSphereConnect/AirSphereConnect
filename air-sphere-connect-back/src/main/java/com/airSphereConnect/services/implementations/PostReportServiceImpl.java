package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.request.PostReportRequestDto;
import com.airSphereConnect.dtos.response.PostReportResponseDto;
import com.airSphereConnect.entities.ForumPost;
import com.airSphereConnect.entities.PostReport;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.entities.enums.UserRole;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.PostReportMapper;
import com.airSphereConnect.repositories.ForumPostRepository;
import com.airSphereConnect.repositories.PostReportRepository;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.PostReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostReportServiceImpl implements PostReportService {

    private final PostReportRepository postReportRepository;
    private final ForumPostRepository forumPostRepository;
    private final UserRepository userRepository;
    private final PostReportMapper postReportMapper;

    public PostReportServiceImpl(PostReportRepository postReportRepository,
                                ForumPostRepository forumPostRepository,
                                UserRepository userRepository,
                                PostReportMapper postReportMapper) {
        this.postReportRepository = postReportRepository;
        this.forumPostRepository = forumPostRepository;
        this.userRepository = userRepository;
        this.postReportMapper = postReportMapper;
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
    }

    private ForumPost findPostByIdOrThrow(Long postId) {
        return forumPostRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Post introuvable : " + postId));
    }

    @Override
    public PostReportResponseDto createReport(PostReportRequestDto request, Long userId) {
        User user = findUserByIdOrThrow(userId);
        ForumPost post = findPostByIdOrThrow(request.postId());

        // Vérifier que l'utilisateur n'a pas déjà signalé ce post
        if (hasUserReportedPost(request.postId(), userId)) {
            throw new GlobalException.UnauthorizedException("Vous avez déjà signalé ce post.");
        }

        // Empêcher un utilisateur de signaler son propre post
        if (post.getUser().getId().equals(userId)) {
            throw new GlobalException.UnauthorizedException("Vous ne pouvez pas signaler votre propre post.");
        }

        PostReport report = postReportMapper.toEntity(request, user, post);
        PostReport savedReport = postReportRepository.save(report);

        return postReportMapper.toResponseDto(savedReport);
    }

    @Override
    @Transactional(readOnly = true)
    public PostReportResponseDto getReportById(Long reportId) {
        PostReport report = postReportRepository.findByIdAndDeletedAtIsNull(reportId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Signalement introuvable : " + reportId));
        return postReportMapper.toResponseDto(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostReportResponseDto> getReportsByPostId(Long postId) {
        List<PostReport> reports = postReportRepository.findByPostIdAndDeletedAtIsNull(postId);
        return reports.stream()
                .map(postReportMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostReportResponseDto> getReportsByUserId(Long userId) {
        List<PostReport> reports = postReportRepository.findByUserIdAndDeletedAtIsNull(userId);
        return reports.stream()
                .map(postReportMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostReportResponseDto> getReportsByStatus(String status) {
        List<PostReport> reports = postReportRepository.findByStatusAndDeletedAtIsNull(status);
        return reports.stream()
                .map(postReportMapper::toResponseDto)
                .toList();
    }

    @Override
    public PostReportResponseDto updateReportStatus(Long reportId, String newStatus, Long adminId) {
        User admin = findUserByIdOrThrow(adminId);

        // Vérifier que c'est un admin
        if (admin.getRole() != UserRole.ADMIN) {
            throw new GlobalException.UnauthorizedException("Seuls les administrateurs peuvent mettre à jour le statut des signalements.");
        }

        PostReport report = postReportRepository.findByIdAndDeletedAtIsNull(reportId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Signalement introuvable : " + reportId));

        report.setStatus(newStatus);
        PostReport updatedReport = postReportRepository.save(report);

        return postReportMapper.toResponseDto(updatedReport);
    }

    @Override
    public void deleteReport(Long reportId, Long userId) {
        PostReport report = postReportRepository.findByIdAndDeletedAtIsNull(reportId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Signalement introuvable : " + reportId));

        User user = findUserByIdOrThrow(userId);

        // Vérifier que c'est soit l'auteur du rapport soit un admin
        boolean isAuthor = report.getUser().getId().equals(userId);
        boolean isAdmin = user.getRole() == UserRole.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new GlobalException.UnauthorizedException("Vous n'êtes pas autorisé à supprimer ce signalement.");
        }

        report.softDelete();
        postReportRepository.save(report);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReportedPost(Long postId, Long userId) {
        return postReportRepository.findByPostIdAndUserIdAndDeletedAtIsNull(postId, userId).isPresent();
    }
}

