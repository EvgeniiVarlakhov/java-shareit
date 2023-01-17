package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureDataJpa
@AutoConfigureTestEntityManager
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void createItemRequest() {
        userRepository.save(
                new User(1L, "user1", "email1@ru"));
        itemRequestRepository.save(
                new ItemRequest(1L, "disc1", 1L, LocalDateTime.now()));
        itemRequestRepository.save(
                new ItemRequest(2L, "disc2", 1L, LocalDateTime.now().plusDays(1)));
    }


    @DirtiesContext
    @Test
    void findAllByRequestorIdOrderByCreatedDesc_whenNotFound_thenReturnEmptyList() {
        long requestorId = 300L;

        Collection<ItemRequest> resultList = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId);

        assertTrue(resultList.isEmpty());
    }

    @DirtiesContext
    @Test
    void findAllByRequestorIdOrderByCreatedDesc_whenFound_thenReturnList() {
        long requestorId = 1L;

        Collection<ItemRequest> resultList = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId);

        assertEquals(2, resultList.size());
    }

    @DirtiesContext
    @Test
    void findItemRequestById_whenNotFound_thenReturnEmptyOptional() {
        long requestorId = 3L;

        Optional<ItemRequest> result = itemRequestRepository.findItemRequestById(requestorId);

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findItemRequestById_whenFound_thenReturnItemRequest() {
        long requestId = 2L;

        Optional<ItemRequest> result = itemRequestRepository.findItemRequestById(requestId);

        assertTrue(result.isPresent());
        assertEquals("disc2", result.get().getDescription());
    }

    @DirtiesContext
    @Test
    void findAllRequests_whenOwnerRequest_thenReturnEmptyList() {
        long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Collection<ItemRequest> result = itemRequestRepository.findAllRequests(ownerId, pageable).getContent();

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findAllRequests_whenOtherUserRequest_thenReturnListItemRequest() {
        long ownerId = 10L;
        Pageable pageable = PageRequest.of(0, 10);

        Collection<ItemRequest> result = itemRequestRepository.findAllRequests(ownerId, pageable).getContent();

        assertEquals(2, result.size());
    }

}