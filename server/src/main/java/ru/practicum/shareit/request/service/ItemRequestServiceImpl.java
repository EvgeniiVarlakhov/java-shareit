package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutWithReplies;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequestDtoOut createItemRequest(ItemRequestDtoIn itemRequestDtoIn, long requestorId) {
        validateUser(requestorId);
        ItemRequest newItemRequest = new ItemRequest(
                0, itemRequestDtoIn.getDescription(), requestorId, LocalDateTime.now());
        ItemRequest saveItemRequest = itemRequestRepository.save(newItemRequest);
        log.info("Добавлен новый запрос = {}", saveItemRequest);
        return ItemRequestMapper.mapToItemRequestDtoOut(saveItemRequest);
    }

    @Override
    public Collection<ItemRequestDtoOutWithReplies> getListOfItemRequestByRequestor(long requestorId) {
        validateUser(requestorId);
        Collection<ItemRequestDtoOutWithReplies> listOfRequests = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId)) {
            Collection<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
            listOfRequests.add(ItemRequestMapper.mapToItemRequestDtoOutWithReplies(itemRequest, items));
        }
        log.info("Получен список запросов для пользователя c ID = {}. Список = {}", requestorId, listOfRequests);
        return listOfRequests;
    }

    @Override
    public ItemRequestDtoOutWithReplies getItemRequestById(long requestorId, long requestId) {
        validateUser(requestorId);
        Optional<ItemRequest> itemRequest = itemRequestRepository.findItemRequestById(requestId);
        if (itemRequest.isEmpty()) {
            throw new ObjectNotFoundException("Запроса с Id = " + requestId + " не существует.");
        }
        Collection<Item> items = itemRepository.findAllByRequestId(requestId);
        log.info("Получена иформация для запроса c ID = {} для пользователя c ID = {}", requestId, requestorId);
        return ItemRequestMapper.mapToItemRequestDtoOutWithReplies(itemRequest.get(), items);
    }

    @Override
    public Collection<ItemRequestDtoOutWithReplies> getListOfItemRequestByAllUsers(long userId, int start, int size) {
        validateUser(userId);
        Pageable pageable = PageRequest.of(start / size, size);
        Collection<ItemRequestDtoOutWithReplies> listOfRequests = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestRepository.findAllRequests(userId, pageable).getContent()) {
            Collection<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
            listOfRequests.add(ItemRequestMapper.mapToItemRequestDtoOutWithReplies(itemRequest, items));
        }
        log.info("Получена иформация по запросам для пользователя c ID = {}. Список = {}", userId, listOfRequests);
        return listOfRequests;
    }

    private void validateUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Пользователя с ID = " + userId + " не существует.");
        }
    }

}
