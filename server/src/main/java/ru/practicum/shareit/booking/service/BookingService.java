package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoFullOut;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import java.util.Collection;

public interface BookingService {

    BookingDtoFullOut getBookingInfo(long userId, long bookingId);

    Collection<BookingDtoFullOut> getListOfBookingsBooker(long userId, String bookingState, int start, int size);

    Collection<BookingDtoFullOut> getListOfBookingsOwner(long ownerId, String bookingState, int start, int size);

    BookingDtoFullOut createBooking(BookingDtoIn bookingDto, long userId);

    BookingDtoFullOut getApprovedBooking(long bookingId, long userId, String approved);

}
