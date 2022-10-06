package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getAllItem(long userId);

    ItemDto getItemById(long itemId);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long itemId, long userId, ItemDto itemDto);

    Collection<ItemDto> searchItemByName(long userId, String text);

    void deleteItem(long itemId, long userId);
}
