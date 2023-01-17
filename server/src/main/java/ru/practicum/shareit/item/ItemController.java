package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Collection<ItemDtoForOwner> getAllItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", required = false) int start,
            @RequestParam(value = "size", required = false) int size) {
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
            @RequestParam(required = false) String text,
            @RequestParam(value = "from", required = false) int start,
            @RequestParam(value = "size", required = false) int size) {
        return itemService.searchItemByName(userId, text, start, size);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut createComment(@PathVariable long itemId,
                                       @RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestBody CommentDtoIn commentDtoIn) {
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
