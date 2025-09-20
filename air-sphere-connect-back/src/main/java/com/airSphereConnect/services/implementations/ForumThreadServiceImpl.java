package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.request.ForumRubricRequestDto;
import com.airSphereConnect.dtos.request.ForumThreadRequestDto;
import com.airSphereConnect.dtos.response.ForumThreadResponseDto;
import com.airSphereConnect.entities.Forum;
import com.airSphereConnect.entities.ForumRubric;
import com.airSphereConnect.entities.ForumThread;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.ForumRubricMapper;
import com.airSphereConnect.mapper.ForumThreadMapper;
import com.airSphereConnect.repositories.ForumRepository;
import com.airSphereConnect.repositories.ForumRubricRepository;
import com.airSphereConnect.repositories.ForumThreadRepository;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.ForumThreadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class ForumThreadServiceImpl implements ForumThreadService {

    private final ForumRubricRepository forumRubricRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final UserRepository userRepository;
    private final ForumThreadMapper forumThreadMapper;

    private static final int MAX_ACTIVE_THREADS_PER_USER = 5;


    public ForumThreadServiceImpl(ForumRubricRepository forumRubricRepository, ForumThreadRepository forumThreadRepository, UserRepository userRepository, ForumThreadMapper forumThreadMapper) {
        this.forumRubricRepository = forumRubricRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.userRepository = userRepository;
        this.forumThreadMapper = forumThreadMapper;
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
    }


    private ForumThread findForumThreadByIdOrThrow(Long id) {
        return forumThreadRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Fil de discussion introuvable : " + id));
    }

    private ForumRubric findForumRubricByIdOrThrow(Long forumRubricId) {
        return forumRubricRepository.findById(forumRubricId)
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Rubrique non trouvée avec l'ID: " + forumRubricId));
    }

    private void validateThreadCreation(ForumThreadRequestDto request, User user, Forum forum) {
        int activeUserThreads = forumThreadRepository.countByUserIdAndDeletedAtIsNull(user.getId());
        if (activeUserThreads >= MAX_ACTIVE_THREADS_PER_USER) {
            throw new GlobalException.BadRequestException(
                    "Vous avez déjà " + MAX_ACTIVE_THREADS_PER_USER + " fils de discussion actifs. " +
                            "Supprimez-en un pour en créer un nouveau."
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
                .collect(Collectors.toList());
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
        validateThreadCreation(request, user, forumRubric.getForum());

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
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Fil de discussion introuvable : " + id));

        return forumThreadMapper.toResponseDto(thread);
    }

}
