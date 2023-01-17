package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.EnumBookingStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingGateController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getListOfBookingsBooker(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String stateParam,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new EnumBookingStateException("Unknown state: " + stateParam));
        log.info("Get booking for booker with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getListOfBookingsBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getListOfBookingsOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String stateParam,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new EnumBookingStateException("Unknown state: " + stateParam));
        log.info("Get booking for owner with state {}, userId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getListOfBookingsOwner(ownerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingInfo(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking by ID = {}, userId={}", bookingId, userId);
        return bookingClient.getBookingInfo(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid BookingDtoIn bookingDtoIn) {
        log.info("Creating booking {}, userId={}", bookingDtoIn, userId);
        return bookingClient.createBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> doApprovedBooking(@PathVariable long bookingId,
                                                    @RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam String approved) {
        log.info("Approve booking ID = {}, userId={}", bookingId, userId);
        return bookingClient.doApprovedBooking(bookingId, userId, approved);
    }

}
