package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutWithReplies;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    @Test
    void mapToItemRequestDtoOut_whenInvoke_thenReturnItemRequestDtoOut() {
        ItemRequest itemRequest = new ItemRequest(1L, "desc1", 1L, LocalDateTime.now());

        ItemRequestDtoOut resultDtoOut = ItemRequestMapper.mapToItemRequestDtoOut(itemRequest);

        assertEquals(resultDtoOut.getId(), itemRequest.getId());
        assertEquals(resultDtoOut.getDescription(), itemRequest.getDescription());
        assertEquals(resultDtoOut.getCreated(), itemRequest.getCreated());
    }

    @Test
    void mapToItemRequestDtoOutWithReplies_whenInvoke_thenReturnRequestWithListItems() {
        Collection<Item> listOfItems = List.of(
                new Item(1L, "name", "desc", true, 1L, 2L));

        ItemRequest request = new ItemRequest(1L, "desc1", 1L, LocalDateTime.now());

        ItemRequestDtoOutWithReplies result = ItemRequestMapper.mapToItemRequestDtoOutWithReplies(request, listOfItems);

        assertEquals(1, result.getId());
        assertEquals(listOfItems.size(), result.getItems().size());
    }

}