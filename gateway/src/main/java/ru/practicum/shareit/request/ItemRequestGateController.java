package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestGateController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getListOfItemRequestByRequestor(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get list item-request userId={}", userId);
        return itemRequestClient.getListOfItemRequestByRequestor(userId);
    }

    @GetMapping(path = "/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long requestId) {
        log.info("Get item-request userId={}, requestId ={}", userId, requestId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getListOfItemRequestByAllUsers(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive int size) {
        log.info("Get list item-requests with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getListOfItemRequestByAllUsers(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Validated @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        log.info("Create item-request userId={}, request={}", userId, itemRequestDtoIn);
        return itemRequestClient.createItemRequest(itemRequestDtoIn, userId);
    }

}
