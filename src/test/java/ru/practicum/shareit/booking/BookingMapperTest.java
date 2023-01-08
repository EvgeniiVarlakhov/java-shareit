package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoFullOut;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    @Test
    void mapNewBookingFromDto_whenInvoke_thenReturnBooking() {
        long userId = 1L;
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                1L,
                2L,
                "status"
        );

        Booking result = BookingMapper.mapNewBookingFromDto(bookingDtoIn, userId);

        assertEquals(userId, result.getBookerId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void mapToBookingFullOut_whenInvoke_thenReturnBookingDtoFullOut() {
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                1L,
                2L,
                BookingStatus.APPROVED);
        User user = new User();
        user.setId(10L);
        Item item = new Item();
        item.setId(10L);
        item.setName("name1");

        BookingDtoFullOut result = BookingMapper.mapToBookingFullOut(booking, user, item);

        assertEquals(user.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(item.getName(), result.getItem().getName());
    }

}