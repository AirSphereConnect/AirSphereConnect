package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.request.ForumRubricRequestDto;
import com.airSphereConnect.dtos.response.ForumRubricResponseDto;
import com.airSphereConnect.entities.Forum;
import com.airSphereConnect.entities.ForumRubric;
import com.airSphereConnect.entities.ForumThread;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.entities.enums.UserRole;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.ForumRubricMapper;
import com.airSphereConnect.repositories.ForumRepository;
import com.airSphereConnect.repositories.ForumRubricRepository;
import com.airSphereConnect.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForumRubricServiceImpl Test Suite")
public class ForumRubricServiceImplTest {

    @Mock
    private ForumRubricRepository forumRubricRepository;

    @Mock
    private ForumRepository forumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ForumRubricMapper forumRubricMapper;

    @InjectMocks
    private ForumRubricServiceImpl forumRubricService;

    private User testUser;
    private User testAdmin;
    private Forum forum;
    private ForumThread forumThread;
    private ForumRubric forumRubric;
    private ForumRubricRequestDto requestDto;
    private ForumRubricResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // création d'un user
        testUser = new User();
        testUser.setId(1L);
        testUser.setRole(UserRole.USER);

        // création d'un admin
        testAdmin = new User();
        testAdmin.setId(2L);
        testAdmin.setRole(UserRole.ADMIN);

        // création d'un forum
        forum = new Forum();
        forum.setId(1L);
        forum.setTitle("Forum Title");

        // création d'une rubrique
        forumRubric = new ForumRubric();
        forumRubric.setTitle("Forum Rubric Title");
        forumRubric.setId(1L);
        forumRubric.setUser(testAdmin);

        // création d'une request Dto
        requestDto = new ForumRubricRequestDto();
        requestDto.setForumId(1L);
        requestDto.setTitle("Forum Title");
        requestDto.setUserId(1L);

        // création d'une response Dto
        responseDto = new ForumRubricResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Forum Rubric Title");

    }

    @Nested
    @DisplayName("Test pour createRubric")
    class CreateRubricTests {

        @Test
        @DisplayName("Devrait créer une rubrique avec succès")
        void shouldCreateRubricSuccessfully() {
            // Arrange
            when(userRepository.findById(2L)).thenReturn(Optional.of(testAdmin));
            when(forumRepository.findById(1L)).thenReturn(Optional.of(forum));
            when(forumRubricMapper.toEntity(requestDto, testAdmin, forum)).thenReturn(forumRubric);
            when(forumRubricRepository.save(forumRubric)).thenReturn(forumRubric);
            when(forumRubricMapper.toResponseDto(forumRubric)).thenReturn(responseDto);

            // Act
            ForumRubricResponseDto result = forumRubricService.createRubric(requestDto, 2L);

            // Assert
            assertNotNull(result);
            assertEquals(responseDto.getId(), result.getId());
            assertEquals(responseDto.getTitle(), result.getTitle());
            verify(userRepository).findById(2L);
            verify(forumRepository).findById(1L);
            verify(forumRubricRepository).save(forumRubric);
        }
    }

}
