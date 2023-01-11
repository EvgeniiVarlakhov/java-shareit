package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ObjectNotFoundException;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    private ItemRequestDtoIn requestDtoIn;
    private ItemRequest itemRequest;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void createRequest() {
        requestDtoIn = new ItemRequestDtoIn("desc1");
        itemRequest = new ItemRequest(1L, "desc1", 1L, LocalDateTime.now());
    }


    @Test
    void createItemRequest_whenUserNotFound_thenObjectNotFoundException() {
        long userId = 1L;
        ItemRequest newRequest = new ItemRequest(0, requestDtoIn.getDescription(), userId, LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.createItemRequest(requestDtoIn, userId));
        verify(itemRequestRepository, never()).save(newRequest);
    }

    @Test
    void createItemRequest_whenUserFound_thenReturnNewRequestDtoOut() {
        long userId = 1L;
        ItemRequest newItemRequest = new ItemRequest(0, requestDtoIn.getDescription(), userId, LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(newItemRequest);

        ItemRequestDtoOut itemRequestDtoOut = itemRequestService.createItemRequest(requestDtoIn, userId);

        assertEquals(requestDtoIn.getDescription(), itemRequestDtoOut.getDescription());
    }

    @Test
    void getListOfItemRequestByRequestor_whenUserNotFound_thenObjectNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getListOfItemRequestByRequestor(userId));
        verify(itemRequestRepository, never()).findAllByRequestorIdOrderByCreatedDesc(userId);
    }

    @Test
    void getListOfItemRequestByRequestor_whenUserFound_thenReturnListOfItemRequest() {
        long userId = 1L;
        Collection<ItemRequest> returnListOfItemRequest = List.of(itemRequest);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)).thenReturn(returnListOfItemRequest);
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(List.of(new Item()));

        Collection<ItemRequestDtoOutWithReplies> result = itemRequestService.getListOfItemRequestByRequestor(userId);

        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), new ArrayList<>(result).get(0).getId());
    }

    @Test
    void getItemRequestById_whenUserNotFound_thenObjectNotFoundException() {
        long userId = 1L;
        long requestId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getItemRequestById(userId, requestId));
        verify(itemRequestRepository, never()).findItemRequestById(requestId);
        verify(itemRepository, never()).findAllByRequestId(requestId);
    }

    @Test
    void getItemRequestById_whenUserFoundButRequestNotFound_thenObjectNotFoundException() {
        long userId = 1L;
        long requestId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findItemRequestById(requestId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getItemRequestById(userId, requestId));
        verify(itemRequestRepository, times(1)).findItemRequestById(requestId);
        verify(itemRepository, never()).findAllByRequestId(requestId);
    }

    @Test
    void getItemRequestById_whenUserFoundAndRequestFound_thenReturnItemRequest() {
        long userId = 1L;
        long requestId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findItemRequestById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(List.of(new Item()));

        ItemRequestDtoOutWithReplies result = itemRequestService.getItemRequestById(userId, requestId);

        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getListOfItemRequestByAllUsers_whenUserNotFound_thenObjectNotFoundException() {
        long userId = 1L;
        long requestId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getListOfItemRequestByAllUsers(
                userId, start, size));
        verify(itemRequestRepository, never()).findAllRequests(userId, pageable);
        verify(itemRepository, never()).findAllByRequestId(requestId);
    }

    @Test
    void getListOfItemRequestByAllUsers_whenUserFoundAndParamIsOk_thenReturnListOfRequests() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest));
        when(itemRequestRepository.findAllRequests(userId, pageable)).thenReturn(page);
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(List.of(new Item()));

        Collection<ItemRequestDtoOutWithReplies> result =
                itemRequestService.getListOfItemRequestByAllUsers(userId, start, size);

        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), new ArrayList<>(result).get(0).getId());
        assertEquals(1, new ArrayList<>(result).get(0).getItems().size());
    }

}