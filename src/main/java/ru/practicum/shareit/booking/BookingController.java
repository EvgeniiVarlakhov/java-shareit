package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoFullOut;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public Collection<BookingDtoFullOut> getListOfBookingsBooker (@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(value = "state", defaultValue = "ALL",required = false)
                                                  String bookingState){
        return bookingService.getListOfBookingsBooker(userId, bookingState);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoFullOut> getListOfBookingsOwner (@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                                  @RequestParam(value = "state", defaultValue = "ALL",required = false)
                                                                  String bookingState){
        return bookingService.getListOfBookingsOwner(ownerId, bookingState);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoFullOut getBookingInfo(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @PostMapping
    public BookingDtoFullOut createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @Validated @RequestBody BookingDtoIn bookingDto) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoFullOut doApprovedBooking(@PathVariable long bookingId,
                                                @RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam String approved) {
        return bookingService.getApprovedBooking(bookingId, userId, approved);
    }

}
