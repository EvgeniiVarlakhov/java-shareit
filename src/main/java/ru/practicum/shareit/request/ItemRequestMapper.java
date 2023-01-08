package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutWithReplies;

import java.util.ArrayList;
import java.util.Collection;

public class ItemRequestMapper {

    public static ItemRequestDtoOut mapToItemRequestDtoOut(ItemRequest itemRequest) {
        return new ItemRequestDtoOut(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequestDtoOutWithReplies mapToItemRequestDtoOutWithReplies(
            ItemRequest itemRequest, Collection<Item> listOfItems) {
        return new ItemRequestDtoOutWithReplies(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>(listOfItems)
        );
    }

}
