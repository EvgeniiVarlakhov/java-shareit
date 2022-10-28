package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoFullOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking mapNewBookingFromDto(BookingDtoIn bookingDto, Long userId) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItemId(),
                userId,
                BookingStatus.WAITING
        );
    }

    public static BookingDtoFullOut mapToBookingFullOut(Booking booking, User user, Item item) {
        return new BookingDtoFullOut(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus().toString(),
                new BookingDtoFullOut.UserInfo(user.getId()),
                new BookingDtoFullOut.ItemInfo(item.getId(), item.getName())
        );
    }

}
