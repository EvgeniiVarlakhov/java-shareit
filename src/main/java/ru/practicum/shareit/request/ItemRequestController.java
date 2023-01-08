package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutWithReplies;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping
    public Collection<ItemRequestDtoOutWithReplies> getListOfItemRequestByRequestor(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getListOfItemRequestByRequestor(userId);
    }

    @GetMapping(path = "/{requestId}")
    public ItemRequestDtoOutWithReplies getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @PathVariable long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }

    @GetMapping(path = "/all")
    public Collection<ItemRequestDtoOutWithReplies> getListOfItemRequestByAllUsers(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero int start,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive int size) {
        return itemRequestService.getListOfItemRequestByAllUsers(userId, start, size);
    }

    @PostMapping
    public ItemRequestDtoOut createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @Validated @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        return itemRequestService.createItemRequest(itemRequestDtoIn, userId);
    }

}
