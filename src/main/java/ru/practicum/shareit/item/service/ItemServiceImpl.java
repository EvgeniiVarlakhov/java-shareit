package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Collection<ItemDto> getAllItem(long userId) {
        userValidate(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemStorage.getItemListByUser(userId)) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }
        return itemDtoList;
    }

    @Override
    public ItemDto getItemById(long itemId) {
        if (itemStorage.getItemById(itemId) == null) {
            throw new ObjectNotFoundException("Вещи с таким ID не существует.");
        }
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userValidate(userId);
        Item newItem = ItemMapper.fromItemDto(userId, itemDto);
        log.info("Создана новая вещь = {}", newItem);
        return ItemMapper.toItemDto(itemStorage.addItem(userId, newItem));
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        userValidate(userId);
        if (itemStorage.getItemById(itemId).getOwnerId() != userId) {
            throw new ObjectNotFoundException("У пользователя с ID = " + userId + " нет вещи с ID = " + itemId + ".");
        }
        ItemMapper.updateItem(itemStorage.getItemById(itemId), itemDto);
        ItemDto updateItemDto = ItemMapper.toItemDto(itemStorage.getItemById(itemId));
        log.info("Вещь ID = {} успешно обновлена. {}", updateItemDto.getId(), updateItemDto);
        return updateItemDto;
    }

    @Override
    public Collection<ItemDto> searchItemByName(long userId, String text) {
        userValidate(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemStorage.getAllItemList()) {
            if (!text.isBlank() && item.getAvailable()
                    && (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                itemDtoList.add(ItemMapper.toItemDto(item));
            }
        }
        return itemDtoList;
    }

    @Override
    public void deleteItem(long itemId, long userId) {
        userValidate(userId);
        if (itemStorage.getItemById(itemId) == null) {
            throw new ObjectNotFoundException("Вещи с таким ID не существует.");
        }
        if (itemStorage.getItemById(itemId).getOwnerId() != userId) {
            throw new ObjectNotFoundException("У пользователя с ID = " + userId + " нет вещи с ID = " + itemId + ".");
        }
        itemStorage.deleteItem(itemId);
        log.info("Вещь с ID = {} успешно удалена.", itemId);
    }

    private void userValidate(long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new ObjectNotFoundException("Пользователя с таким ID не существует.");
        }
    }

}
