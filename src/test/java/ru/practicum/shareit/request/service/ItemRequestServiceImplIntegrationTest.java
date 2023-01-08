package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutWithReplies;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {
    private final ItemRequestService itemRequestService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void createdUnite() {
        userRepository.save(new User(0L, "owner", "owner@ru"));
        userRepository.save(new User(0L, "user", "user@ru"));
    }

    @DirtiesContext
    @Test
    void createItemRequest_whenInvoke_thenSaveNewRequest() {
        long userId = 1L;
        ItemRequestDtoIn newRequest = new ItemRequestDtoIn("new Request");

        assertTrue(itemRequestRepository.findAll().isEmpty());

        ItemRequestDtoOut result = itemRequestService.createItemRequest(newRequest, userId);

        assertEquals(1, itemRequestRepository.findAll().size());
        assertEquals(newRequest.getDescription(), result.getDescription());
    }

    @DirtiesContext
    @Test
    void getListOfItemRequestByRequestor_whenNotItemByTheRequest_thenReturnItemRequestWithoutItems() {
        long userId = 2L;
        itemRepository.save(new Item(0L, "name", "desc", true, 1L, null));
        itemRequestRepository.save(new ItemRequest(0L, "request", 2L, LocalDateTime.now()));
        itemRequestRepository.save(new ItemRequest(0L, "request", 1L, LocalDateTime.now()));

        Collection<ItemRequestDtoOutWithReplies> result = itemRequestService.getListOfItemRequestByRequestor(userId);

        assertEquals(1, result.size());
        assertEquals("request", new ArrayList<>(result).get(0).getDescription());
        assertTrue(new ArrayList<>(result).get(0).getItems().isEmpty());
    }

    @DirtiesContext
    @Test
    void getListOfItemRequestByRequestor_whenItemByTheRequest_thenReturnItemRequestWitItems() {
        long userId = 2L;
        itemRepository.save(new Item(0L, "name", "desc", true, 1L, 1L));
        itemRequestRepository.save(new ItemRequest(0L, "request", 2L, LocalDateTime.now()));
        itemRequestRepository.save(new ItemRequest(0L, "request", 1L, LocalDateTime.now()));

        Collection<ItemRequestDtoOutWithReplies> result = itemRequestService.getListOfItemRequestByRequestor(userId);

        assertEquals(1, result.size());
        assertEquals("request", new ArrayList<>(result).get(0).getDescription());
        assertEquals(1, new ArrayList<>(result).get(0).getItems().size());
        assertEquals("name", new ArrayList<>(result).get(0).getItems().get(0).getName());
    }

    @DirtiesContext
    @Test
    void getItemRequestById_whenInvoke_thenReturnRequestWhithReplies() {
        long userId = 2L;
        itemRepository.save(new Item(0L, "name", "desc", true, 1L, 1L));
        itemRequestRepository.save(new ItemRequest(0L, "request", 2L, LocalDateTime.now()));

        ItemRequestDtoOutWithReplies result = itemRequestService.getItemRequestById(userId, 1L);

        assertEquals("request", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("name", result.getItems().get(0).getName());
    }

    @DirtiesContext
    @Test
    void getListOfItemRequestByAllUsers_whenInvoke_thenReturnAllRequestExceptHisRequest() {
        long userId = 2L;
        itemRepository.save(new Item(0L, "name", "desc", true, 1L, 1L));
        itemRequestRepository.save(new ItemRequest(0L, "request", 2L, LocalDateTime.now()));
        itemRequestRepository.save(new ItemRequest(0L, "request", 1L, LocalDateTime.now()));
        itemRequestRepository.save(new ItemRequest(0L, "request", 1L, LocalDateTime.now()));

        Collection<ItemRequestDtoOutWithReplies> result =
                itemRequestService.getListOfItemRequestByAllUsers(userId, 0, 10);

        assertEquals(2, result.size());
    }

}