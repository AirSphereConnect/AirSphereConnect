package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.request.ForumPostRequestDto;
import com.airsphereconnect.dtos.response.ForumPostResponseDto;
import com.airsphereconnect.entities.ForumPost;
import com.airsphereconnect.entities.ForumThread;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.entities.enums.UserRole;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.ForumPostMapper;
import com.airsphereconnect.repositories.ForumPostRepository;
import com.airsphereconnect.repositories.ForumThreadRepository;
import com.airsphereconnect.repositories.UserRepository;
import com.airsphereconnect.services.ForumPostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ForumPostServiceImpl implements ForumPostService {

    private final ForumPostRepository forumPostRepository;
    private final ForumThreadRepository forumThreadRepository;
    private final UserRepository userRepository;
    private final ForumPostMapper forumPostMapper;

    public ForumPostServiceImpl(ForumPostRepository forumPostRepository, ForumThreadRepository forumThreadRepository, UserRepository userRepository, ForumPostMapper forumPostMapper) {
        this.forumPostRepository = forumPostRepository;
        this.forumThreadRepository = forumThreadRepository;
        this.userRepository = userRepository;
        this.forumPostMapper = forumPostMapper;
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
    }

    private ForumThread findForumThreadByIdOrThrow(Long threadId) {
        return forumThreadRepository.findByIdAndDeletedAtIsNull(threadId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Fil de discussion introuvable : " + threadId));
    }

    private ForumPost findForumPostByIdOrThrow(Long id) {
        return forumPostRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Post introuvable : " + id));
    }

    private void validatePostAuthor(ForumPost post, Long userId) {
        User user = userRepository.findById(userId).orElseThrow( () ->
                new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));


        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        if (!isAdmin && !post.getUser().getId().equals(userId)) {
            throw new GlobalException.UnauthorizedException("Vous n'êtes pas autorisé à modifier ce post.");
        }
    }


    @Override
    @Transactional(readOnly = true)
    public ForumPostResponseDto getPostById(Long id) {
        ForumPost post = forumPostRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Post introuvable : " + id));
        return forumPostMapper.toResponseDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumPostResponseDto> getAllActivePosts() {
        List<ForumPost> posts = forumPostRepository.findAllWithRelations();
        return posts.stream()
                .map(forumPostMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumPostResponseDto> getPostsByThreadId(Long threadId) {
        List<ForumPost> posts = forumPostRepository.findByThreadIdWithRelations(threadId);
        return posts.stream()
                .map(forumPostMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumPostResponseDto> getPostsByUserId(Long userId) {
        List<ForumPost> posts = forumPostRepository.findByUserIdWithRelations(userId);
        return posts.stream()
                .map(forumPostMapper::toResponseDto)
                .toList();
    }


    @Override
    public ForumPostResponseDto createPost(ForumPostRequestDto request, Long userId) {
        User user = findUserByIdOrThrow(userId);
        ForumThread forumThread = findForumThreadByIdOrThrow(request.getThreadId());

        ForumPost post = forumPostMapper.toEntity(request, user, forumThread);
        ForumPost savedPost = forumPostRepository.save(post);

        return forumPostMapper.toResponseDto(savedPost);
    }

    @Override
    public ForumPostResponseDto updatePost(Long id, ForumPostRequestDto request, Long userId) {
        ForumPost existingPost = findForumPostByIdOrThrow(id);
        validatePostAuthor(existingPost, userId);

        existingPost.setContent(request.getContent());
        ForumPost updatedPost = forumPostRepository.save(existingPost);

        return forumPostMapper.toResponseDto(updatedPost);
    }

    @Override
    public void deletePost(Long id, Long userId) {
        ForumPost existingPost = findForumPostByIdOrThrow(id);
        validatePostAuthor(existingPost, userId);

        existingPost.softDelete();
        forumPostRepository.save(existingPost);
    }



}
