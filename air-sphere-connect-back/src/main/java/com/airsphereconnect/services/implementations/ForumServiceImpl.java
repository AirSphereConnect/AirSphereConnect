package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.response.ForumResponseDto;
import com.airsphereconnect.dtos.response.ForumRubricResponseDto;
import com.airsphereconnect.entities.Forum;
import com.airsphereconnect.entities.ForumRubric;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.ForumMapper;
import com.airsphereconnect.mapper.ForumRubricMapper;
import com.airsphereconnect.repositories.ForumRepository;
import com.airsphereconnect.repositories.ForumRubricRepository;
import com.airsphereconnect.services.ForumService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumServiceImpl implements ForumService {

    private final ForumRepository forumRepository;
    private final ForumMapper forumMapper;
    private final ForumRubricRepository forumRubricRepository;
    private final ForumRubricMapper forumRubricMapper;

    public ForumServiceImpl(ForumRepository forumRepository, ForumMapper forumMapper, ForumRubricRepository forumRubricRepository, ForumRubricMapper forumRubricMapper) {
        this.forumRepository = forumRepository;
        this.forumMapper = forumMapper;
        this.forumRubricRepository = forumRubricRepository;
        this.forumRubricMapper = forumRubricMapper;
    }


    @Override
    @Transactional(readOnly = true)
    public ForumResponseDto getForumById(Long id) {
        Forum forum = forumRepository.findByIdWithRubrics(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Forum non trouvé"));

        return forumMapper.toResponseDto(forum);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumRubricResponseDto> getRubricsByForumId(Long id) {
        List<ForumRubric> rubrics = forumRubricRepository.findByForumIdAndDeletedAtIsNull(id);
        return rubrics.stream()
                .map(forumRubricMapper::toResponseDto)
                .toList();
    }

}
