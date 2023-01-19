package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoFullOut;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public Collection<BookingDtoFullOut> getListOfBookingsBooker(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "state", required = false) String bookingState,
            @RequestParam(value = "from", required = false) int start,
            @RequestParam(value = "size", required = false) int size) {
        return bookingService.getListOfBookingsBooker(userId, bookingState, start, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoFullOut> getListOfBookingsOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(value = "state", required = false) String bookingState,
            @RequestParam(value = "from", required = false) int start,
            @RequestParam(value = "size", required = false) int size) {
        return bookingService.getListOfBookingsOwner(ownerId, bookingState, start, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoFullOut getBookingInfo(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @PostMapping
    public BookingDtoFullOut createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody BookingDtoIn bookingDto) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoFullOut doApprovedBooking(@PathVariable long bookingId,
                                               @RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam String approved) {
        return bookingService.getApprovedBooking(bookingId, userId, approved);
    }

}
