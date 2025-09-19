package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.request.ForumRubricRequestDto;
import com.airSphereConnect.dtos.response.ForumRubricResponseDto;
import com.airSphereConnect.entities.Forum;
import com.airSphereConnect.entities.ForumRubric;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.ForumRubricMapper;
import com.airSphereConnect.repositories.ForumRepository;
import com.airSphereConnect.repositories.ForumRubricRepository;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.ForumRubricService;
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
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
    }


    private ForumRubric findForumRubricByIdOrThrow(Long rubricId) {
       return forumRubricRepository.findByIdAndDeletedAtIsNull(rubricId)
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Rubrique introuvable : " + rubricId));
    }

    private Forum findForumByIdOrThrow(Long forumId) {
        return forumRepository.findById(forumId)
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Forum non trouvé avec l'ID: " + forumId));
    }

    private void validateRubricCreation(ForumRubricRequestDto request, User user, Forum forum) {
        int activeUserRubrics = forumRubricRepository.countByUserIdAndDeletedAtIsNull(user.getId());
        if (activeUserRubrics >= MAX_ACTIVE_RUBRICS_PER_USER) {
            throw new GlobalException.BadRequestException(
                    "Vous avez déjà " + MAX_ACTIVE_RUBRICS_PER_USER + " rubriques actives. " +
                            "Supprimez-en une pour en créer une nouvelle."
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
        List<ForumRubric> rubrics = forumRubricRepository.findByUserIdAndDeletedAtIsNull(userId);
        return rubrics.stream()
                .map(forumRubricMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ForumRubricResponseDto> getRubricsByForumId(Long forumId) {
        Forum forum = findForumByIdOrThrow(forumId);
        List<ForumRubric> rubrics = forumRubricRepository.findByForumIdAndDeletedAtIsNull(forumId);
        return rubrics.stream()
                .map(forumRubricMapper::toResponseDto)
                .toList();
    }

    @Override
    public ForumRubricResponseDto createRubric(ForumRubricRequestDto request, Long userId) {
        User user = findUserByIdOrThrow(userId);
        Forum forum = findForumByIdOrThrow(request.getForumId());


        validateRubricCreation(request, user, forum);


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
    public ForumRubricResponseDto getRubricWithAllDetails(Long id, Long userId) {
        ForumRubric rubric = findForumRubricByIdOrThrow(id);
        return forumRubricMapper.toResponseDto(rubric);
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
