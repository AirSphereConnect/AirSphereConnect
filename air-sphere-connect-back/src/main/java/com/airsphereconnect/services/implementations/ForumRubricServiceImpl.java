package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.request.ForumRubricRequestDto;
import com.airsphereconnect.dtos.response.ForumRubricResponseDto;
import com.airsphereconnect.entities.Forum;
import com.airsphereconnect.entities.ForumRubric;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.ForumRubricMapper;
import com.airsphereconnect.repositories.ForumRepository;
import com.airsphereconnect.repositories.ForumRubricRepository;
import com.airsphereconnect.repositories.UserRepository;
import com.airsphereconnect.services.ForumRubricService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class ForumRubricServiceImpl implements ForumRubricService {

    private final ForumRubricRepository forumRubricRepository;
    private final ForumRepository forumRepository;
    private final UserRepository userRepository;
    private final ForumRubricMapper forumRubricMapper;

    private static final int MAX_ACTIVE_RUBRICS_PER_USER = 5;
    private static final int MAX_ACTIVE_RUBRICS_PER_FORUM = 20;

    public ForumRubricServiceImpl(ForumRubricRepository forumRubricRepository,
                                  ForumRepository forumRepository,
                                  UserRepository userRepository,
                                  ForumRubricMapper forumRubricMapper) {
        this.forumRubricRepository = forumRubricRepository;
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
        this.forumRubricMapper = forumRubricMapper;

    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
    }


    private ForumRubric findForumRubricByIdOrThrow(Long id) {
       return forumRubricRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Rubrique introuvable : " + id));
    }

    private Forum findForumByIdOrThrow(Long forumId) {
        return forumRepository.findById(forumId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Forum non trouvé avec l'ID: " + forumId));
    }

    private void validateRubricCreation(User user, Forum forum, ForumRubricRequestDto request) {
        int activeUserRubrics = forumRubricRepository.countByUserIdAndDeletedAtIsNull(user.getId());
        if (activeUserRubrics >= MAX_ACTIVE_RUBRICS_PER_USER) {
            throw new GlobalException.BadRequestException(
                    "Vous avez déjà " + MAX_ACTIVE_RUBRICS_PER_USER + " rubriques actives. " +
                            "Supprimez-en une pour en créer une nouvelle."
            );
        }

        int activeForumRubrics = forumRubricRepository.countByForumIdAndDeletedAtIsNull(forum.getId());
        if (activeForumRubrics >= MAX_ACTIVE_RUBRICS_PER_FORUM) {
            throw new GlobalException.BadRequestException(
                    "Ce forum a atteint la limite de " + MAX_ACTIVE_RUBRICS_PER_FORUM + " rubriques actives."
            );
        }

        if (forumRubricRepository.existsByForumIdAndTitleIgnoreCaseAndDeletedAtIsNull(forum.getId(), request.getTitle())) {
            throw new GlobalException.BadRequestException(
                    "Une rubrique avec ce titre existe déjà dans ce forum."
            );
        }
    }



    @Override
    @Transactional(readOnly = true)
    public ForumRubricResponseDto getRubricById(Long rubricId) {
        ForumRubric rubric = findForumRubricByIdOrThrow(rubricId);
        return forumRubricMapper.toResponseDto(rubric);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumRubricResponseDto> getAllActiveRubrics() {
        List<ForumRubric> rubrics = forumRubricRepository.findAllByDeletedAtIsNull();
        return rubrics.stream()
                .map(forumRubricMapper::toResponseDto)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<ForumRubricResponseDto> getRubricsByCurrentUser(Long userId) {
        User user = findUserByIdOrThrow(userId);
        List<ForumRubric> rubrics = forumRubricRepository.findByUserIdAndDeletedAtIsNull(user.getId());
        return rubrics.stream()
                .map(forumRubricMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumRubricResponseDto> getRubricsByForumId(Long forumId) {
        Forum forum = findForumByIdOrThrow(forumId);
        List<ForumRubric> rubrics = forumRubricRepository.findByForumIdAndDeletedAtIsNull(forum.getId());
        return rubrics.stream()
                .map(forumRubricMapper::toResponseDto)
                .toList();
    }

    @Override
    public ForumRubricResponseDto createRubric(ForumRubricRequestDto request, Long userId) {
        User user = findUserByIdOrThrow(userId);
        Forum forum = findForumByIdOrThrow(request.getForumId());

        validateRubricCreation(user, forum, request);

        ForumRubric rubric = forumRubricMapper.toEntity(request, user, forum);
        ForumRubric savedRubric = forumRubricRepository.save(rubric);
        return forumRubricMapper.toResponseDto(savedRubric);
    }


    @Override
    public ForumRubricResponseDto updateRubric(Long id, ForumRubricRequestDto request, Long userId) {
        ForumRubric existingRubric = findForumRubricByIdOrThrow(id);
        User user = findUserByIdOrThrow(userId);

        if (!existingRubric.getUser().getId().equals(user.getId())) {
            throw new GlobalException.UnauthorizedException("Vous n'êtes pas autorisé à modifier cette rubrique.");
        }

        existingRubric.setTitle(request.getTitle());
        existingRubric.setDescription(request.getDescription());

        ForumRubric updatedRubric = forumRubricRepository.save(existingRubric);
        return forumRubricMapper.toResponseDto(updatedRubric);
    }

    @Override
    public void deleteRubric(Long id, Long userId) {
        User user = findUserByIdOrThrow(userId);
        ForumRubric existingRubric = findForumRubricByIdOrThrow(id);

        if (!existingRubric.getUser().getId().equals(user.getId())) {
            throw new GlobalException.UnauthorizedException("Vous n'êtes pas autorisé à supprimer cette rubrique.");
        }

        existingRubric.softDelete();
        forumRubricRepository.save(existingRubric);
    }

    @Override
    @Transactional(readOnly = true)
    public ForumRubricResponseDto getRubricWithAllDetails(Long id, Long userId) {
        User user = findUserByIdOrThrow(userId);

        ForumRubric rubric = forumRubricRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Rubrique introuvable: " + id));

        ForumRubricResponseDto response = forumRubricMapper.toResponseDto(rubric);

        countRubricsByUser(user.getId());
        countRubricsByForum(rubric.getForum().getId());

        return response;
    }


    @Override
    public int countRubricsByUser(Long userId) {
        User user = findUserByIdOrThrow(userId);
        return forumRubricRepository.countByUserIdAndDeletedAtIsNull(user.getId());
    }

    @Override
    public int countRubricsByForum(Long forumId) {
        Forum forum = findForumByIdOrThrow(forumId);
        return forumRubricRepository.countByForumIdAndDeletedAtIsNull(forum.getId());
    }

    @Override
    public List<ForumRubric> findByForumIdOrderByCreatedAtAsc(Long forumId) {
        return forumRubricRepository.findByForumIdAndDeletedAtIsNullOrderByCreatedAtAsc(forumId);
    }

    @Override
    public List<ForumRubric> findByForumIdOrderByCreatedAtDesc(Long forumId) {
        return forumRubricRepository.findByForumIdAndDeletedAtIsNullOrderByCreatedAtDesc(forumId);
    }

    @Override
    public List<ForumRubric> findByUserIdOrderByCreatedAtAsc(Long userId) {
        return forumRubricRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtAsc(userId);
    }

    @Override
    public List<ForumRubric> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return forumRubricRepository.findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId);
    }
}
