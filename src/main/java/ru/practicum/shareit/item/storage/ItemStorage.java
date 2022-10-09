package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item addItem(long idOwner, Item item);

    void deleteItem(long itemId);

    Item getItemById(long itemId);

    Collection<Item> getItemListByUser(long userId);

    Collection<Item> getAllItemsList();
}

