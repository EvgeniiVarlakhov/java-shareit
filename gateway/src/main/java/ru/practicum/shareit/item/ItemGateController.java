package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemGateController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive int size) {
        log.info("Get all items with userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        log.info("Get item by ID={}, userId={}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByName(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = " ", required = false) String text,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive int size) {
        log.info("Search all items with text={}, userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.searchItemByName(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Validated({CreateItem.class}) @RequestBody ItemDto itemDto) {
        log.info("Create item userId={}, item ={}", userId, itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable long itemId,
                                                @RequestHeader("X-Sharer-User-Id") long userId,
                                                @Validated @RequestBody CommentDtoIn commentDtoIn) {
        log.info("Create comment userId={}, itemID={}, comment={}", userId, itemId, commentDtoIn);
        return itemClient.createComment(itemId, userId, commentDtoIn);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Update item with itemID={}, userId={}, item ={}", itemId, userId, itemDto);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Delete item userId={}, itemId ={}", userId, itemId);
        return itemClient.deleteItem(itemId, userId);
    }

}
