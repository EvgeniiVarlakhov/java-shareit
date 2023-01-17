package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoFullOut;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {
    private final BookingService bookingService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void createdUnite() {
        userRepository.save(new User(0L, "owner", "owner@ru"));
        userRepository.save(new User(0L, "booker", "booker@ru"));
        userRepository.save(new User(0L, "user", "user@ru"));
        itemRepository.save(new Item(0L, "item-1", "desc-1", true, 1L, null));
        itemRepository.save(new Item(0L, "item-2", "desc-2", false, 1L, null));
    }

    @DirtiesContext
    @Test
    void getBookingInfo_whenUserIsNotOwnerOrBooker_thenReturnObjectNotFoundException() {
        long userId = 3L;
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking);

        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBookingInfo(userId, 1L));
    }

    @DirtiesContext
    @Test
    void getBookingInfo_whenUserIsOwner_thenReturnBookingInfo() {
        long userId = 1L;
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking);

        BookingDtoFullOut result = bookingService.getBookingInfo(userId, 1L);

        assertEquals(1L, result.getId());
        assertEquals(booking.getItemId(), result.getItem().getId());
        assertEquals(booking.getBookerId(), result.getBooker().getId());
    }

    @DirtiesContext
    @Test
    void getBookingInfo_whenUserIsBooker_thenReturnBookingInfo() {
        long userId = 2L;
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking);

        BookingDtoFullOut result = bookingService.getBookingInfo(userId, 1L);

        assertEquals(1L, result.getId());
        assertEquals(booking.getItemId(), result.getItem().getId());
        assertEquals(booking.getBookerId(), result.getBooker().getId());
    }

    @DirtiesContext
    @Test
    void getListOfBookingsBooker_whenGetAllBookings_thenReturnAllBookings() {
        String state = "ALL";
        long userId = 2L;
        Booking booking1 = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(10), 1L, 2L, BookingStatus.WAITING);
        Booking booking2 = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(20), 2L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsBooker(userId, state, 0, 10);

        assertEquals(2, result.size());
    }

    @DirtiesContext
    @Test
    void getListOfBookingsBooker_whenGetCurrentBookings_thenReturnBooking1() {
        String state = "CURRENT";
        long userId = 2L;
        Booking booking1 = new Booking(0L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10), 1L, 2L, BookingStatus.WAITING);
        Booking booking2 = new Booking(0L, LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(20), 2L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsBooker(userId, state, 0, 10);

        assertEquals(1, result.size());
        assertEquals(1L, new ArrayList<>(result).get(0).getId());
    }

    @DirtiesContext
    @Test
    void getListOfBookingsBooker_whenGetPastBookings_thenReturnBooking1() {
        String state = "PAST";
        long userId = 2L;
        Booking booking1 = new Booking(0L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1), 1L, 2L, BookingStatus.WAITING);
        Booking booking2 = new Booking(0L, LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(20), 2L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsBooker(userId, state, 0, 10);

        assertEquals(1, result.size());
        assertEquals(1L, new ArrayList<>(result).get(0).getId());
    }

    @DirtiesContext
    @Test
    void getListOfBookingsBooker_whenGetFutureBookings_thenReturnBooking2() {
        String state = "FUTURE";
        long userId = 2L;
        Booking booking1 = new Booking(0L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1), 1L, 2L, BookingStatus.WAITING);
        Booking booking2 = new Booking(0L, LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(20), 2L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsBooker(userId, state, 0, 10);

        assertEquals(1, result.size());
        assertEquals(2L, new ArrayList<>(result).get(0).getId());
    }

    @DirtiesContext
    @Test
    void getListOfBookingsBooker_whenGetWaitingBookings_thenReturnBothBookings() {
        String state = "WAITING";
        long userId = 2L;
        Booking booking1 = new Booking(0L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1), 1L, 2L, BookingStatus.WAITING);
        Booking booking2 = new Booking(0L, LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(20), 2L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsBooker(userId, state, 0, 10);

        assertEquals(2, result.size());
        assertEquals(2L, new ArrayList<>(result).get(0).getId());
        assertEquals(1L, new ArrayList<>(result).get(1).getId());
    }

    @DirtiesContext
    @Test
    void getListOfBookingsBooker_whenGetRejectedBookings_thenReturnBooking3() {
        String state = "REJECTED";
        long userId = 2L;
        Booking booking1 = new Booking(0L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1), 1L, 2L, BookingStatus.WAITING);
        Booking booking2 = new Booking(0L, LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(20), 2L, 2L, BookingStatus.WAITING);
        Booking booking3 = new Booking(0L, LocalDateTime.now().plusDays(16), LocalDateTime.now().plusDays(21), 2L, 2L, BookingStatus.REJECTED);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsBooker(userId, state, 0, 10);

        assertEquals(1, result.size());
        assertEquals(3L, new ArrayList<>(result).get(0).getId());
    }

    @DirtiesContext
    @Test
    void getListOfBookingsOwner_whenGetAllBookings_thenReturnAllBookings() {
        String state = "ALL";
        long userId = 1L;
        Booking booking1 = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(10), 1L, 2L, BookingStatus.WAITING);
        Booking booking2 = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(20), 2L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsOwner(userId, state, 0, 10);

        assertEquals(2, result.size());
    }

    @DirtiesContext
    @Test
    void getListOfBookingsOwner_whenGetCurrentBookings_thenReturnBooking1() {
        String state = "CURRENT";
        long userId = 1L;
        Booking booking1 = new Booking(0L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10), 1L, 2L, BookingStatus.WAITING);
        Booking booking2 = new Booking(0L, LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(20), 2L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsOwner(userId, state, 0, 10);

        assertEquals(1, result.size());
        assertEquals(1L, new ArrayList<>(result).get(0).getId());
    }

    @DirtiesContext
    @Test
    void getListOfBookingsOwner_whenGetPastBookings_thenReturnBooking1() {
        String state = "PAST";
        long userId = 1L;
        Booking booking1 = new Booking(0L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1), 1L, 2L, BookingStatus.WAITING);
        Booking booking2 = new Booking(0L, LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(20), 2L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsOwner(userId, state, 0, 10);

        assertEquals(1, result.size());
        assertEquals(1L, new ArrayList<>(result).get(0).getId());
    }

    @DirtiesContext
    @Test
    void getListOfBookingsOwner_whenGetFutureBookings_thenReturnBooking2() {
        String state = "FUTURE";
        long userId = 1L;
        Booking booking1 = new Booking(0L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1), 1L, 2L, BookingStatus.WAITING);
        Booking booking2 = new Booking(0L, LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(20), 2L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsOwner(userId, state, 0, 10);

        assertEquals(1, result.size());
        assertEquals(2L, new ArrayList<>(result).get(0).getId());
    }

    @DirtiesContext
    @Test
    void getListOfBookingsOwner_whenGetWaitingBookings_thenReturnBothBookings() {
        String state = "WAITING";
        long userId = 1L;
        Booking booking1 = new Booking(0L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1), 1L, 2L, BookingStatus.WAITING);
        Booking booking2 = new Booking(0L, LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(20), 2L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsOwner(userId, state, 0, 10);

        assertEquals(2, result.size());
        assertEquals(2L, new ArrayList<>(result).get(0).getId());
        assertEquals(1L, new ArrayList<>(result).get(1).getId());
    }

    @DirtiesContext
    @Test
    void getListOfBookingsOwner_whenGetRejectedBookings_thenReturnBooking3() {
        String state = "REJECTED";
        long userId = 1L;
        Booking booking1 = new Booking(0L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1), 1L, 2L, BookingStatus.WAITING);
        Booking booking2 = new Booking(0L, LocalDateTime.now().plusDays(15), LocalDateTime.now().plusDays(20), 2L, 2L, BookingStatus.WAITING);
        Booking booking3 = new Booking(0L, LocalDateTime.now().plusDays(16), LocalDateTime.now().plusDays(21), 2L, 2L, BookingStatus.REJECTED);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsOwner(userId, state, 0, 10);

        assertEquals(1, result.size());
        assertEquals(3L, new ArrayList<>(result).get(0).getId());
    }

    @DirtiesContext
    @Test
    void createBooking_whenInvoke_thenReturnNewBooking() {
        long bookerId = 2L;
        BookingDtoIn newBooking = new BookingDtoIn(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 2L, "Status");

        BookingDtoFullOut result = bookingService.createBooking(newBooking, bookerId);

        assertEquals(1L, result.getId());
        assertEquals(BookingState.WAITING.toString(), result.getStatus());
        assertEquals(1, bookingRepository.findAll().size());
    }

    @DirtiesContext
    @Test
    void getApprovedBooking_whenApproveIsTrue_thenReturnStatusApproved() {
        long ownerId = 1L;
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking);

        BookingDtoFullOut resul = bookingService.getApprovedBooking(1L, ownerId, "true");

        assertEquals(BookingStatus.APPROVED.toString(), resul.getStatus());
    }

    @DirtiesContext
    @Test
    void getApprovedBooking_whenApproveIsFalse_thenReturnStatusRejected() {
        long ownerId = 1L;
        Booking booking = new Booking(0L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 2L, BookingStatus.WAITING);
        bookingRepository.save(booking);

        BookingDtoFullOut resul = bookingService.getApprovedBooking(1L, ownerId, "false");

        assertEquals(BookingStatus.REJECTED.toString(), resul.getStatus());
    }

}