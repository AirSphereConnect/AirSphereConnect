package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.response.ForumResponseDto;
import com.airSphereConnect.dtos.response.ForumRubricResponseDto;
import com.airSphereConnect.entities.Forum;
import com.airSphereConnect.entities.ForumRubric;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.ForumMapper;
import com.airSphereConnect.mapper.ForumRubricMapper;
import com.airSphereConnect.repositories.ForumRepository;
import com.airSphereConnect.repositories.ForumRubricRepository;
import com.airSphereConnect.services.ForumService;
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
        Forum forum = forumRepository.findById(id)
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Forum non trouvé"));

        return forumMapper.toResponseDto(forum);
    }

    @Override
    public List<ForumRubricResponseDto> getRubricsByForumId(Long forumId) {
        List<ForumRubric> rubrics = forumRubricRepository.findByForumIdAndDeletedAtIsNull(forumId);
        return rubrics.stream()
                .map(forumRubricMapper::toResponseDto)
                .toList();
    }

}
