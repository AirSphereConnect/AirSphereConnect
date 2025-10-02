package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.request.ForumPostRequestDto;
import com.airSphereConnect.dtos.response.ForumPostResponseDto;
import com.airSphereConnect.entities.ForumPost;
import com.airSphereConnect.entities.ForumThread;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.ForumPostMapper;
import com.airSphereConnect.repositories.ForumPostRepository;
import com.airSphereConnect.repositories.ForumThreadRepository;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.ForumPostService;
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
        if (!post.getUser().getId().equals(userId)) {
            throw new GlobalException.UnauthorizedException("Vous n'êtes pas autorisé à modérer ce post.");
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
