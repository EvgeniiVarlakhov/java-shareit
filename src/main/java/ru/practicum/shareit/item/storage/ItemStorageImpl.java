package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class ItemStorageImpl implements ItemStorage {
    private long idNumber = 1;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(long idOwner, Item item) {
        item.setId(idNumber);
        item.setOwnerId(idOwner);
        items.put(idNumber, item);
        Item createdItem = items.get(idNumber);
        idNumber++;
        return createdItem;
    }

    @Override
    public void deleteItem(long itemId) {
        items.remove(itemId);
    }

    @Override
    public Item getItemById(long itemId) {
        return items.getOrDefault(itemId, null);
    }

    @Override
    public Collection<Item> getItemListByUser(long userId) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId() == userId) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    @Override
    public Collection<Item> getAllItemsList() {
        return new ArrayList<>(items.values());
    }

}
