package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.request.ForumThreadRequestDto;
import com.airsphereconnect.dtos.response.ForumThreadResponseDto;
import com.airsphereconnect.entities.ForumRubric;
import com.airsphereconnect.entities.ForumThread;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.ForumThreadMapper;
import com.airsphereconnect.repositories.ForumRubricRepository;
import com.airsphereconnect.repositories.ForumThreadRepository;
import com.airsphereconnect.repositories.UserRepository;
import com.airsphereconnect.services.ForumThreadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class ForumThreadServiceImpl implements ForumThreadService {

    private final ForumRubricRepository forumRubricRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final UserRepository userRepository;
    private final ForumThreadMapper forumThreadMapper;

    private static final int MAX_ACTIVE_THREADS_PER_USER = 5;
    private static final int MAX_ACTIVE_THREADS_PER_RUBRIC = 10;


    public ForumThreadServiceImpl(ForumRubricRepository forumRubricRepository, ForumThreadRepository forumThreadRepository, UserRepository userRepository, ForumThreadMapper forumThreadMapper) {
        this.forumRubricRepository = forumRubricRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.userRepository = userRepository;
        this.forumThreadMapper = forumThreadMapper;
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
    }


    private ForumThread findForumThreadByIdOrThrow(Long id) {
        return forumThreadRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Fil de discussion introuvable : " + id));
    }

    private ForumRubric findForumRubricByIdOrThrow(Long forumRubricId) {
        return forumRubricRepository.findById(forumRubricId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Rubrique non trouvée avec l'ID: " + forumRubricId));
    }

    private void validateThreadCreation(User user, ForumRubric forumRubric, ForumThreadRequestDto request) {
        int activeUserThreads = forumThreadRepository.countByUserIdAndDeletedAtIsNull(user.getId());
        if (activeUserThreads >= MAX_ACTIVE_THREADS_PER_USER) {
            throw new GlobalException.BadRequestException(
                    "Vous avez déjà " + MAX_ACTIVE_THREADS_PER_USER + " fils de discussion actifs. " +
                            "Supprimez-en un pour en créer un nouveau."
            );
        }

        int activeRubricThreads = forumThreadRepository.countByForumRubricIdAndDeletedAtIsNull(forumRubric.getId());
        if (activeRubricThreads >= MAX_ACTIVE_THREADS_PER_RUBRIC) {
            throw new GlobalException.BadRequestException(
                    "Cette rubrique a atteint la limite de " + MAX_ACTIVE_THREADS_PER_RUBRIC + " fils de discussion actifs."
            );
        }

        if (forumThreadRepository.existsByForumRubricIdAndTitleIgnoreCaseAndDeletedAtIsNull(forumRubric.getId(), request.getTitle())) {
            throw new GlobalException.BadRequestException(
                    "Un fil de discussion avec ce titre existe déjà dans cette rubrique."
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ForumThreadResponseDto getThreadById(Long threadId) {
        ForumThread thread = findForumThreadByIdOrThrow(threadId);
        return forumThreadMapper.toResponseDto(thread);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumThreadResponseDto> getAllActiveThreads() {
        List<ForumThread> threads = forumThreadRepository.findAllByDeletedAtIsNull();
        return threads.stream()
                .map(forumThreadMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumThreadResponseDto> getThreadsByCurrentUser(Long userId) {
        User user = findUserByIdOrThrow(userId);
        List<ForumThread> threads = forumThreadRepository.findByUserIdAndDeletedAtIsNull(user.getId());
        return threads.stream()
                .map(forumThreadMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumThreadResponseDto> getThreadsByForumRubricId(Long forumRubricId) {
        ForumRubric forumRubric = findForumRubricByIdOrThrow(forumRubricId);
        List<ForumThread> threads = forumThreadRepository.findByForumRubricIdAndDeletedAtIsNull(forumRubric.getId());
        return threads.stream()
                .map(forumThreadMapper::toResponseDto)
                .toList();

    }

    @Override
    public ForumThreadResponseDto createThread(ForumThreadRequestDto request, Long userId) {
        User user = findUserByIdOrThrow(userId);
        ForumRubric forumRubric = findForumRubricByIdOrThrow(request.getRubricId());
        validateThreadCreation(user, forumRubric, request);

        ForumThread thread = forumThreadMapper.toEntity(request, user, forumRubric);
        ForumThread savedThread = forumThreadRepository.save(thread);
        return forumThreadMapper.toResponseDto(savedThread);
    }

    @Override
    public ForumThreadResponseDto updateThread(Long id, ForumThreadRequestDto request, Long userId) {
        ForumThread existingThread = findForumThreadByIdOrThrow(id);
        User user = findUserByIdOrThrow(userId);

        if (!existingThread.getUser().getId().equals(user.getId())) {
            throw new GlobalException.UnauthorizedException("Vous n'êtes pas autorisé à modifier ce fil de discussion.");
        }

        existingThread.setTitle(request.getTitle());

        ForumThread updatedThread = forumThreadRepository.save(existingThread);
        return forumThreadMapper.toResponseDto(updatedThread);

    }

    @Override
    public void deleteThread(Long id, Long userId) {
        User user = findUserByIdOrThrow(userId);
        ForumThread existingThread = findForumThreadByIdOrThrow(id);

        if (!existingThread.getUser().getId().equals(user.getId())) {
            throw new GlobalException.UnauthorizedException("Vous n'êtes pas autorisé à supprimer ce fil de discussion.");
        }

        existingThread.softDelete();
        forumThreadRepository.save(existingThread);
    }

    @Override
    @Transactional(readOnly = true)
    public ForumThreadResponseDto getThreadWithAllDetails(Long id, Long userId) {
        User user = findUserByIdOrThrow(userId);

        ForumThread thread = forumThreadRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Fil de discussion introuvable : " + id));

        ForumThreadResponseDto response = forumThreadMapper.toResponseDto(thread);

        countThreadsByUser(user.getId());
        countThreadsByRubric(thread.getForumRubric().getId());
        return response;
    }


    public int countThreadsByRubric(Long rubricId) {
        ForumRubric forumRubric = findForumRubricByIdOrThrow(rubricId);
        return forumThreadRepository.countByForumRubricIdAndDeletedAtIsNull(forumRubric.getId());
    }

    public int countThreadsByUser(Long userId) {
        User user = findUserByIdOrThrow(userId);
        return forumThreadRepository.countByUserIdAndDeletedAtIsNull(user.getId());
    }

}
