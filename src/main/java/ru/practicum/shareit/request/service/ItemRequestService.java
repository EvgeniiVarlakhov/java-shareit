package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutWithReplies;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDtoOut createItemRequest(ItemRequestDtoIn itemRequestDtoIn, long requestorId);

    Collection<ItemRequestDtoOutWithReplies> getListOfItemRequestByRequestor(long requestorId);

    ItemRequestDtoOutWithReplies getItemRequestById(long userId, long requestId);

    Collection<ItemRequestDtoOutWithReplies> getListOfItemRequestByAllUsers(long userId, int start, int size);
}
