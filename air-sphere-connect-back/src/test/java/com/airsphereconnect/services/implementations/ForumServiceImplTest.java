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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForumServiceImpl Test Suite")
class ForumServiceImplTest {

    @Mock
    private ForumRepository forumRepository;

    @Mock
    private ForumMapper forumMapper;

    @Mock
    private ForumRubricRepository forumRubricRepository;

    @Mock
    private ForumRubricMapper forumRubricMapper;

    @InjectMocks
    private ForumServiceImpl forumService;

    private Forum testForum;
    private ForumRubric testRubric;
    private ForumResponseDto testForumResponseDto;
    private ForumRubricResponseDto testRubricResponseDto;

    @BeforeEach
    void setUp() {
        testForum = new Forum();
        testForum.setId(1L);
        testForum.setTitle("Forum Test");

        testRubric = new ForumRubric();
        testRubric.setId(1L);
        testRubric.setTitle("Rubrique Test");
        testRubric.setForum(testForum);

        testForumResponseDto = new ForumResponseDto();
        testForumResponseDto.setId(1L);
        testForumResponseDto.setTitle("Forum Test");

        testRubricResponseDto = new ForumRubricResponseDto();
        testRubricResponseDto.setId(1L);
        testRubricResponseDto.setTitle("Rubrique Test");
    }

    @Nested
    @DisplayName("Tests pour getForumById")
    class GetForumByIdTests {

        @Test
        @DisplayName("Devrait récupérer un forum avec succès")
        void shouldGetForumByIdSuccessfully() {
            when(forumRepository.findByIdWithRubrics(1L)).thenReturn(Optional.of(testForum));
            when(forumMapper.toResponseDto(testForum)).thenReturn(testForumResponseDto);

            ForumResponseDto result = forumService.getForumById(1L);

            assertNotNull(result);
            assertEquals(testForumResponseDto.getId(), result.getId());
            assertEquals(testForumResponseDto.getTitle(), result.getTitle());
        }

        @Test
        @DisplayName("Devrait échouer si le forum n'existe pas")
        void shouldThrowExceptionWhenForumNotFound() {
            when(forumRepository.findByIdWithRubrics(999L)).thenReturn(Optional.empty());

            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> forumService.getForumById(999L)
            );

            assertTrue(exception.getMessage().contains("Forum non trouvé"));
        }
    }

    @Nested
    @DisplayName("Tests pour getRubricsByForumId")
    class GetRubricsByForumIdTests {

        @Test
        @DisplayName("Devrait retourner les rubriques d'un forum")
        void shouldReturnRubricsByForumId() {
            when(forumRubricRepository.findByForumIdAndDeletedAtIsNull(1L)).thenReturn(List.of(testRubric));
            when(forumRubricMapper.toResponseDto(testRubric)).thenReturn(testRubricResponseDto);

            List<ForumRubricResponseDto> result = forumService.getRubricsByForumId(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testRubricResponseDto.getId(), result.get(0).getId());
        }

        @Test
        @DisplayName("Devrait retourner une liste vide si aucune rubrique")
        void shouldReturnEmptyListWhenNoRubrics() {
            when(forumRubricRepository.findByForumIdAndDeletedAtIsNull(1L)).thenReturn(List.of());

            List<ForumRubricResponseDto> result = forumService.getRubricsByForumId(1L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
