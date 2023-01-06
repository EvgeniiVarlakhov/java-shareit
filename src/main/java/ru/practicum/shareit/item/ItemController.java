package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAbstract;
import ru.practicum.shareit.item.dto.ItemDtoForBooker;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Collection<ItemDtoForOwner> getAllItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0", required = false) int start,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        return itemService.getAllItems(userId, start, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoAbstract getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDtoForBooker> searchItemByName(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = " ", required = false) String text,
            @RequestParam(value = "from", defaultValue = "0", required = false) int start,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        return itemService.searchItemByName(userId, text, start, size);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Validated({CreateItem.class}) @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut createComment(@PathVariable long itemId,
                                       @RequestHeader("X-Sharer-User-Id") long userId,
                                       @Validated @RequestBody CommentDtoIn commentDtoIn) {
        return itemService.createComment(itemId, userId, commentDtoIn);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        itemService.deleteItem(itemId, userId);
    }

}
