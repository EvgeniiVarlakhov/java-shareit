package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoFullOut;

import java.util.Collection;

public interface BookingService {

    BookingDtoFullOut getBookingInfo(long userId, long bookerId);

    Collection<BookingDtoFullOut> getListOfBookingsBooker(long userId, String bookingState);

    Collection<BookingDtoFullOut> getListOfBookingsOwner(long ownerId, String bookingState);

    BookingDtoFullOut createBooking(BookingDtoIn bookingDto, long userId);

    BookingDtoFullOut getApprovedBooking(long bookingId, long userId, String approved);

}
