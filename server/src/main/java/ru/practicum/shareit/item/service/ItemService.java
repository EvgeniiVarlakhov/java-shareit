package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDtoForOwner> getAllItems(long userId, int start, int size);

    ItemDtoAbstract getItemById(long userId, long itemId);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long itemId, long userId, ItemDto itemDto);

    Collection<ItemDtoForBooker> searchItemByName(long userId, String text, int start, int size);

    void deleteItem(long itemId, long userId);

    CommentDtoOut createComment(long itemId, long userId, CommentDtoIn commentDtoIn);

}
