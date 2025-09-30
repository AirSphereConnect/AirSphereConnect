package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.ForumPost;
import com.airSphereConnect.entities.PostReaction;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.entities.enums.ReactionType;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.ForumPostRepository;
import com.airSphereConnect.repositories.PostReactionRepository;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.PostReactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PostReactionServiceImpl implements PostReactionService {
    private final PostReactionRepository postReactionRepository;
    private final ForumPostRepository forumPostRepository;
    private final UserRepository userRepository;


    public PostReactionServiceImpl(PostReactionRepository postReactionRepository, ForumPostRepository forumPostRepository, UserRepository userRepository) {
        this.postReactionRepository = postReactionRepository;
        this.forumPostRepository = forumPostRepository;
        this.userRepository = userRepository;
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
    }


    private ForumPost findForumPostByIdOrThrow(Long id) {
        return forumPostRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Post introuvable : " + id));
    }


    @Override
    public void toggleReaction(Long postId, Long userId, ReactionType newReaction) {
        ForumPost post = findForumPostByIdOrThrow(postId);
        User user = findUserByIdOrThrow(userId);

        Optional<PostReaction> existingReactionOpt = postReactionRepository
                .findByPostIdAndUserId(postId, userId);

        if (existingReactionOpt.isPresent()) {
            PostReaction existingReaction = existingReactionOpt.get();

            if (existingReaction.getDeletedAt() != null) {
                existingReaction.setDeletedAt(null);
                existingReaction.setReactionType(newReaction);
            } else if (existingReaction.getReactionType() == newReaction) {
                existingReaction.softDelete();
            } else {
                existingReaction.setReactionType(newReaction);
            }
            postReactionRepository.save(existingReaction);
        } else {
            // Create
            PostReaction newPostReaction = new PostReaction(user, post, newReaction);
            postReactionRepository.save(newPostReaction);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countLikesByPost(Long postId) {
        return postReactionRepository.countByPostIdAndReactionType(postId, ReactionType.LIKE);
    }

    @Override
    @Transactional(readOnly = true)
    public long countDislikesByPost(Long postId) {
        return postReactionRepository.countByPostIdAndReactionType(postId, ReactionType.DISLIKE);
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionType getUserReaction(Long postId, Long userId) {
        return postReactionRepository.findUserReaction(postId, userId);
    }

}
