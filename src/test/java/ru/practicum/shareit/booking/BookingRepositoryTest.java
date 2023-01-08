package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void createBd() {
        Booking booking1 = new Booking(
                0L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(11),
                1L,
                2L,
                BookingStatus.WAITING);
        Booking booking2 = new Booking(
                0L,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(22),
                2L,
                2L,
                BookingStatus.APPROVED);
        Item item1 = new Item(0L, "item1", "desc1", true, 1L, null);
        Item item2 = new Item(0L, "item2", "desc2", true, 1L, null);
        User owner = new User(0L, "owner", "owner@ru");
        User booker = new User(0L, "booker", "booker@ru");

        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item1);
        itemRepository.save(item2);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
    }

    @DirtiesContext
    @Test
    void findByBookerIdOrderByStartDesc_whenBookingNotFound_thenReturnEmptyList() {
        long bookerId = 100L;

        Collection<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(
                bookerId, Pageable.ofSize(10)).getContent();

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findByBookerIdOrderByStartDesc_whenBookingFound_thenReturnSortListByStart() {
        long bookerId = 2L;

        Collection<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(
                bookerId, Pageable.ofSize(10)).getContent();

        assertEquals(2, result.size());
        assertEquals(2, new ArrayList<>(result).get(0).getItemId());
    }

    @DirtiesContext
    @Test
    void findByBookerIdAndStartAfterOrderByStartDesc_whenAfterStartNotFound_thenReturnEmptyList() {
        long bookerId = 2L;
        LocalDateTime start = LocalDateTime.now().plusDays(100);

        Collection<Booking> result = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                bookerId, start, Pageable.ofSize(10)).getContent();

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findByBookerIdAndStartAfterOrderByStartDesc_whenAfterStartBetweenBookings_thenReturnListSizeOne() {
        long bookerId = 2L;
        LocalDateTime start = LocalDateTime.now().plusDays(3);

        Collection<Booking> result = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                bookerId, start, Pageable.ofSize(10)).getContent();

        assertEquals(1, result.size());
        assertEquals(2, new ArrayList<>(result).get(0).getItemId());
    }

    @DirtiesContext
    @Test
    void findByBookerIdAndStartAfterOrderByStartDesc_whenAfterStartBeforeBookings_thenReturnListSizeTwo() {
        long bookerId = 2L;
        LocalDateTime start = LocalDateTime.now();

        Collection<Booking> result = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                bookerId, start, Pageable.ofSize(10)).getContent();

        assertEquals(2, result.size());
        assertEquals(2, new ArrayList<>(result).get(0).getItemId());
        assertEquals(1, new ArrayList<>(result).get(1).getItemId());
    }

    @DirtiesContext
    @Test
    void findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc_whenInvoke_ReturnBooking2() {
        long bookerId = 2L;
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(10);

        Collection<Booking> result = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                bookerId, start, end, Pageable.ofSize(10)).getContent();

        assertEquals(1, result.size());
        assertEquals(1, new ArrayList<>(result).get(0).getItemId());
    }

    @DirtiesContext
    @Test
    void findByBookerIdAndEndIsBeforeOrderByStartDesc_whenEndAfterEndBookingsInBd_thenReturnAllBookings() {
        long bookerId = 2;
        LocalDateTime end = LocalDateTime.now().plusDays(50);

        Collection<Booking> result = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                bookerId, end, Pageable.ofSize(10)).getContent();

        assertEquals(2, result.size());
        assertEquals(2, new ArrayList<>(result).get(0).getItemId());
        assertEquals(1, new ArrayList<>(result).get(1).getItemId());
    }

    @DirtiesContext
    @Test
    void findByBookerIdAndEndIsBeforeOrderByStartDesc_whenEndBetweenEndBookingsInBd_thenReturnBooking1() {
        long bookerId = 2;
        LocalDateTime end = LocalDateTime.now().plusDays(15);

        Collection<Booking> result = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                bookerId, end, Pageable.ofSize(10)).getContent();

        assertEquals(1, result.size());
        assertEquals(1, new ArrayList<>(result).get(0).getItemId());
    }

    @DirtiesContext
    @Test
    void findByBookerIdAndStatusEqualsOrderByStartDesc_whenStatusIsApproved_thenReturnBooking2() {
        long bookerId = 2L;
        BookingStatus status = BookingStatus.APPROVED;

        Collection<Booking> result = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                bookerId, status, Pageable.ofSize(10)).getContent();

        assertEquals(1, result.size());
        assertEquals(2, new ArrayList<>(result).get(0).getItemId());
    }

    @DirtiesContext
    @Test
    void findAllBookingsForOwner_whenBookerRequest_thenReturnEmptyList() {
        long bookerId = 2L;

        Collection<Booking> result = bookingRepository.findAllBookingsForOwner(
                bookerId, Pageable.ofSize(10)).getContent();

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findAllBookingsForOwner_whenOwnerRequest_thenReturnListSizeTwo() {
        long ownerId = 1L;

        Collection<Booking> result = bookingRepository.findAllBookingsForOwner(
                ownerId, Pageable.ofSize(10)).getContent();

        assertEquals(2, result.size());
    }

    @DirtiesContext
    @Test
    void findCurrentBookingsForOwner_whenTimeCorrectForBooking2_thenReturnBooking2() {
        long ownerId = 1L;
        LocalDateTime time = LocalDateTime.now().plusDays(18);

        Collection<Booking> result = bookingRepository.findCurrentBookingsForOwner(
                ownerId, time, Pageable.ofSize(10)).getContent();

        assertEquals(1, result.size());
        assertEquals(2, new ArrayList<>(result).get(0).getItemId());
    }

    @DirtiesContext
    @Test
    void findCurrentBookingsForOwner_whenTimeCorrectForAllBookings_thenReturnBookings() {
        long ownerId = 1L;
        LocalDateTime time = LocalDateTime.now().plusDays(10);

        Collection<Booking> result = bookingRepository.findCurrentBookingsForOwner(
                ownerId, time, Pageable.ofSize(10)).getContent();

        assertEquals(2, result.size());
        assertEquals(2, new ArrayList<>(result).get(0).getItemId());
        assertEquals(1, new ArrayList<>(result).get(1).getItemId());
    }

    @DirtiesContext
    @Test
    void findPastBookingsForOwner_whenTimeBeforeEndBooking2_thenReturnBooking1() {
        long ownerId = 1L;
        LocalDateTime time = LocalDateTime.now().plusDays(20);

        Collection<Booking> result = bookingRepository.findPastBookingsForOwner(
                ownerId, time, Pageable.ofSize(10)).getContent();

        assertEquals(1, result.size());
        assertEquals(1, new ArrayList<>(result).get(0).getItemId());
    }

    @DirtiesContext
    @Test
    void findPastBookingsForOwner_whenTimeAfterEndBooking2_thenReturnAllBookings() {
        long ownerId = 1L;
        LocalDateTime time = LocalDateTime.now().plusDays(25);

        Collection<Booking> result = bookingRepository.findPastBookingsForOwner(
                ownerId, time, Pageable.ofSize(10)).getContent();

        assertEquals(2, result.size());
        assertEquals(2, new ArrayList<>(result).get(0).getItemId());
        assertEquals(1, new ArrayList<>(result).get(1).getItemId());
    }

    @DirtiesContext
    @Test
    void findFutureBookingsForOwner_whenTimeAfterAllBookings_thenReturnEmptyList() {
        long ownerId = 1L;
        LocalDateTime time = LocalDateTime.now().plusDays(25);

        Collection<Booking> result = bookingRepository.findFutureBookingsForOwner(
                ownerId, time, Pageable.ofSize(10)).getContent();

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findFutureBookingsForOwner_whenTimeBeforeAllBookings_thenReturnAllBookings() {
        long ownerId = 1L;
        LocalDateTime time = LocalDateTime.now();

        Collection<Booking> result = bookingRepository.findFutureBookingsForOwner(
                ownerId, time, Pageable.ofSize(10)).getContent();

        assertEquals(2, result.size());
        assertEquals(2, new ArrayList<>(result).get(0).getItemId());
        assertEquals(1, new ArrayList<>(result).get(1).getItemId());
    }

    @DirtiesContext
    @Test
    void findStatusBookingsForOwner_whenBookerRequest_returnEmptyList() {
        long bookerId = 2L;
        String status = BookingStatus.WAITING.toString();

        Collection<Booking> result = bookingRepository.findStatusBookingsForOwner(
                bookerId, status, Pageable.ofSize(10)).getContent();

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findStatusBookingsForOwner_whenOwnerRequest_returnBooking1() {
        long ownerId = 1L;
        String status = BookingStatus.WAITING.toString();

        Collection<Booking> result = bookingRepository.findStatusBookingsForOwner(
                ownerId, status, Pageable.ofSize(10)).getContent();

        assertEquals(1, result.size());
        assertEquals(1, new ArrayList<>(result).get(0).getItemId());
    }

    @DirtiesContext
    @Test
    void findLastBookingByItem_whenTimeAfterEndBookingItem1_thenReturnOptionalIsEmpty() {
        long itemId = 1L;
        LocalDateTime time = LocalDateTime.now().plusDays(9);

        Optional<Booking> result = bookingRepository.findLastBookingByItem(itemId, time);

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findLastBookingByItem_whenTimeBeforeEndBookingItem1_thenReturnBooking1() {
        long itemId = 1L;
        LocalDateTime time = LocalDateTime.now().plusDays(12);

        Optional<Booking> result = bookingRepository.findLastBookingByItem(itemId, time);

        assertEquals(1, result.get().getId());
        assertEquals(1, result.get().getItemId());
    }

    @DirtiesContext
    @Test
    void findNextBookingByItem_whenTimeAfterStartBooking2_thenReturnOptionalIsEmpty() {
        long itemId = 2L;
        LocalDateTime time = LocalDateTime.now().plusDays(10);

        Optional<Booking> result = bookingRepository.findNextBookingByItem(itemId, time);

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findNextBookingByItem_whenTimeBeforeStartBooking2_thenReturnBooking2() {
        long itemId = 2L;
        LocalDateTime time = LocalDateTime.now().plusDays(2);

        Optional<Booking> result = bookingRepository.findNextBookingByItem(itemId, time);

        assertEquals(2, result.get().getId());
        assertEquals(2, result.get().getItemId());
    }

    @DirtiesContext
    @Test
    void findBookingByItemIdAndBookerIdAndEndIsBefore_whenTimeBeforeEndBooking1_thenReturnOptionalIsEmpty() {
        long userId = 2L;
        long itemId = 1L;
        LocalDateTime time = LocalDateTime.now().plusDays(5);

        Optional<Booking> result = bookingRepository.findBookingByItemIdAndBookerIdAndEndIsBefore(itemId, userId, time);

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void findBookingByItemIdAndBookerIdAndEndIsBefore_whenTimeAfterEndBooking1_thenReturnBooking1() {
        long userId = 2L;
        long itemId = 1L;
        LocalDateTime time = LocalDateTime.now().plusDays(18);

        Optional<Booking> result = bookingRepository.findBookingByItemIdAndBookerIdAndEndIsBefore(itemId, userId, time);

        assertEquals(1, result.get().getId());
    }

}