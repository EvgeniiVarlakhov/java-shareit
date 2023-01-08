package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoFullOut;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EnumBookingStateException;
import ru.practicum.shareit.exception.InvalidValidationException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private User owner;
    private User user;
    private Item item;
    private Booking booking;
    private BookingDtoIn bookingDtoIn;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void createUnits() {
        owner = new User(1L, "owner", "owner@ru");
        user = new User(2L, "user", "user@ru");
        item = new Item(1L, "item", "desc1", true, 1L, null);
        booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                1L,
                2L,
                BookingStatus.WAITING
        );

        bookingDtoIn = new BookingDtoIn(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                1L,
                2L,
                null
        );

    }

    @Test
    void getBookingInfo_whenUserNotFound_thenObjectNotFoundException() {
        long userId = 1L;
        long bookingId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBookingInfo(userId, bookingId));

        verify(bookingRepository, never()).findById(bookingId);
    }

    @Test
    void getBookingInfo_whenBookingNotFound_thenObjectNotFoundException() {
        long userId = 1L;
        long bookingId = 1L;
        long itemId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBookingInfo(userId, bookingId));

        verify(itemRepository, never()).findById(itemId);
    }

    @Test
    void getBookingInfo_whenItemNotFound_thenObjectNotFoundException() {
        long userId = 1L;
        long bookingId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(new Booking()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBookingInfo(userId, bookingId));
    }

    @Test
    void getBookingInfo_whenNotOwnerAndBookerTryGetInfo_thenObjectNotFoundException() {
        long userId = 5L;
        long bookingId = 1L;
        item.setOwnerId(3L);
        booking.setBookerId(4L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));

        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBookingInfo(userId, bookingId));
    }

    @Test
    void getBookingInfo_whenOwnerTryGetInfo_thenReturnBookingDTtoFullOut() {
        long userId = 3L;
        long bookingId = 1L;
        item.setOwnerId(3L);
        booking.setBookerId(4L);
        BookingDtoFullOut expected = BookingMapper.mapToBookingFullOut(booking, owner, item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));

        BookingDtoFullOut result = bookingService.getBookingInfo(userId, bookingId);

        assertEquals(expected, result);
    }

    @Test
    void getBookingInfo_whenBookerTryGetInfo_thenReturnBookingDTtoFullOut() {
        long userId = 4L;
        long bookingId = 1L;
        item.setOwnerId(3L);
        booking.setBookerId(4L);
        BookingDtoFullOut expected = BookingMapper.mapToBookingFullOut(booking, owner, item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItemId())).thenReturn(Optional.of(item));

        BookingDtoFullOut result = bookingService.getBookingInfo(userId, bookingId);

        assertEquals(expected, result);
    }

    @Test
    void getListOfBookingsBooker_whenStateIsNotCorrect_thenEnumBookingStateException() {
        long bookerId = 2L;
        int start = 0;
        int size = 10;
        String state = "state";

        assertThrows(EnumBookingStateException.class,
                () -> bookingService.getListOfBookingsBooker(bookerId, state, start, size));
    }

    @Test
    void getListOfBookingsBooker_whenBookerNotFound_thenInvalidValidationException() {
        long bookerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.ALL.toString();
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getListOfBookingsBooker(bookerId, state, start, size));
    }

    @Test
    void getListOfBookingsBooker_whenStateAll_thenReturnListOfBookingDtoFullOuts() {
        long bookerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.ALL.toString();
        Pageable pageable = PageRequest.of(start / size, size);
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, user, item);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Collection<BookingDtoFullOut> expectedList = List.of(expectedBooking);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable)).thenReturn(page);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsBooker(bookerId, state, start, size);

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void getListOfBookingsBooker_whenStateCurrent_thenReturnListOfBookingDtoFullOuts() {
        long bookerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.CURRENT.toString();
        Pageable pageable = PageRequest.of(start / size, size);
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, user, item);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Collection<BookingDtoFullOut> expectedList = List.of(expectedBooking);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                eq(bookerId),
                any(),
                any(),
                eq(pageable)))
                .thenReturn(page);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsBooker(bookerId, state, start, size);

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void getListOfBookingsBooker_whenPastCurrent_thenReturnListOfBookingDtoFullOuts() {
        long bookerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.PAST.toString();
        Pageable pageable = PageRequest.of(start / size, size);
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, user, item);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Collection<BookingDtoFullOut> expectedList = List.of(expectedBooking);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                eq(bookerId),
                any(),
                eq(pageable)))
                .thenReturn(page);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsBooker(bookerId, state, start, size);

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void getListOfBookingsBooker_whenFutureCurrent_thenReturnListOfBookingDtoFullOuts() {
        long bookerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.FUTURE.toString();
        Pageable pageable = PageRequest.of(start / size, size);
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, user, item);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Collection<BookingDtoFullOut> expectedList = List.of(expectedBooking);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                eq(bookerId),
                any(),
                eq(pageable)))
                .thenReturn(page);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsBooker(bookerId, state, start, size);

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void getListOfBookingsBooker_whenWaitingCurrent_thenReturnListOfBookingDtoFullOuts() {
        long bookerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.WAITING.toString();
        Pageable pageable = PageRequest.of(start / size, size);
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, user, item);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Collection<BookingDtoFullOut> expectedList = List.of(expectedBooking);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                bookerId,
                BookingStatus.WAITING,
                pageable))
                .thenReturn(page);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsBooker(bookerId, state, start, size);

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void getListOfBookingsBooker_whenRejectedCurrent_thenReturnListOfBookingDtoFullOuts() {
        long bookerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.REJECTED.toString();
        Pageable pageable = PageRequest.of(start / size, size);
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, user, item);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Collection<BookingDtoFullOut> expectedList = List.of(expectedBooking);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                bookerId,
                BookingStatus.REJECTED,
                pageable))
                .thenReturn(page);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsBooker(bookerId, state, start, size);

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void getListOfBookingsOwner_whenStateIsNotCorrect_thenEnumBookingStateException() {
        long ownerId = 2L;
        int start = 0;
        int size = 10;
        String state = "state";

        assertThrows(EnumBookingStateException.class,
                () -> bookingService.getListOfBookingsOwner(ownerId, state, start, size));
    }

    @Test
    void getListOfBookingsOwner_whenOwnerNotFound_thenObjectNotFoundException() {
        long ownerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.ALL.toString();
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getListOfBookingsOwner(ownerId, state, start, size));
    }

    @Test
    void getListOfBookingsOwner_whenOwnerHaveNotAnyItem_thenObjectNotFoundException() {
        long ownerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.ALL.toString();
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, owner, item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdIsOrderById(ownerId, PageRequest.of(0, 1))).thenReturn(Page.empty());

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getListOfBookingsOwner(ownerId, state, start, size));
    }

    @Test
    void getListOfBookingsOwner_whenStateAll_thenReturnListOfBookingDtoFullOuts() {
        long ownerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.ALL.toString();
        Pageable pageable = PageRequest.of(start / size, size);
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, owner, item);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Collection<BookingDtoFullOut> expectedList = List.of(expectedBooking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdIsOrderById(ownerId, PageRequest.of(0, 1))).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findAllBookingsForOwner(ownerId, pageable)).thenReturn(page);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsOwner(ownerId, state, start, size);

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void getListOfBookingsOwner_whenStateCurrent_thenReturnListOfBookingDtoFullOuts() {
        long ownerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.CURRENT.toString();
        Pageable pageable = PageRequest.of(start / size, size);
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, owner, item);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Collection<BookingDtoFullOut> expectedList = List.of(expectedBooking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdIsOrderById(ownerId, PageRequest.of(0, 1))).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findCurrentBookingsForOwner(eq(ownerId), any(), eq(pageable))).thenReturn(page);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsOwner(ownerId, state, start, size);

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void getListOfBookingsOwner_whenStatePast_thenReturnListOfBookingDtoFullOuts() {
        long ownerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.PAST.toString();
        Pageable pageable = PageRequest.of(start / size, size);
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, owner, item);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Collection<BookingDtoFullOut> expectedList = List.of(expectedBooking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdIsOrderById(ownerId, PageRequest.of(0, 1))).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findPastBookingsForOwner(eq(ownerId), any(), eq(pageable))).thenReturn(page);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsOwner(ownerId, state, start, size);

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void getListOfBookingsOwner_whenStateFuture_thenReturnListOfBookingDtoFullOuts() {
        long ownerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.FUTURE.toString();
        Pageable pageable = PageRequest.of(start / size, size);
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, owner, item);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Collection<BookingDtoFullOut> expectedList = List.of(expectedBooking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdIsOrderById(ownerId, PageRequest.of(0, 1))).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findFutureBookingsForOwner(eq(ownerId), any(), eq(pageable))).thenReturn(page);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsOwner(ownerId, state, start, size);

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void getListOfBookingsOwner_whenStateWaiting_thenReturnListOfBookingDtoFullOuts() {
        long ownerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.WAITING.toString();
        Pageable pageable = PageRequest.of(start / size, size);
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, owner, item);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Collection<BookingDtoFullOut> expectedList = List.of(expectedBooking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdIsOrderById(ownerId, PageRequest.of(0, 1))).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findStatusBookingsForOwner(ownerId, BookingStatus.WAITING.toString(), pageable)).thenReturn(page);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsOwner(ownerId, state, start, size);

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void getListOfBookingsOwner_whenStateReject_thenReturnListOfBookingDtoFullOuts() {
        long ownerId = 2L;
        int start = 0;
        int size = 10;
        String state = BookingState.REJECTED.toString();
        Pageable pageable = PageRequest.of(start / size, size);
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, owner, item);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        Collection<BookingDtoFullOut> expectedList = List.of(expectedBooking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdIsOrderById(ownerId, PageRequest.of(0, 1))).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findStatusBookingsForOwner(ownerId, BookingStatus.REJECTED.toString(), pageable)).thenReturn(page);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Collection<BookingDtoFullOut> result = bookingService.getListOfBookingsOwner(ownerId, state, start, size);

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
    }

    @Test
    void createBooking_whenUserNotFound_thenObjectNotFoundException() {
        long bookerId = 2L;
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.createBooking(bookingDtoIn, bookerId));
    }

    @Test
    void createBooking_whenBookingDtoStartAfterEnd_thenNotAvailableException() {
        long bookerId = 2L;
        bookingDtoIn.setEnd(LocalDateTime.now());
        bookingDtoIn.setStart(LocalDateTime.now().plusDays(10));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));

        assertThrows(NotAvailableException.class, () -> bookingService.createBooking(bookingDtoIn, bookerId));
    }

    @Test
    void createBooking_whenItemNotFound_thenObjectNotFoundException() {
        long bookerId = 2L;
        long itemId = 1L;
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.createBooking(bookingDtoIn, bookerId));
    }

    @Test
    void createBooking_whenItemNotAvailable_thenObjectNotFoundException() {
        long bookerId = 2L;
        long itemId = 1L;
        item.setAvailable(false);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotAvailableException.class, () -> bookingService.createBooking(bookingDtoIn, bookerId));
    }

    @Test
    void createBooking_whenBookerIsOwner_thenObjectNotFoundException() {
        long bookerId = 2L;
        long itemId = 1L;
        item.setOwnerId(2L);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ObjectNotFoundException.class, () -> bookingService.createBooking(bookingDtoIn, bookerId));
    }

    @Test
    void createBooking_whenValidateIsOk_thenReturnBookingDtoFullOut() {
        long bookerId = 2L;
        long itemId = 1L;
        BookingDtoFullOut expectedBooking = BookingMapper.mapToBookingFullOut(booking, user, item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoFullOut result = bookingService.createBooking(bookingDtoIn, bookerId);

        assertEquals(expectedBooking, result);
    }

    @Test
    void getApprovedBooking_whenBookingNotFound_thenObjectNotFoundException() {
        long ownerId = 1L;
        long bookingId = 1L;
        String approved = "false";
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getApprovedBooking(bookingId, ownerId, approved));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getApprovedBooking_whenOwnerNotFound_thenObjectNotFoundException() {
        long ownerId = 1L;
        long bookingId = 1L;
        String approved = "false";
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getApprovedBooking(bookingId, ownerId, approved));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getApprovedBooking_whenUserIsNotOwner_thenObjectNotFoundException() {
        long ownerId = 1L;
        long bookingId = 1L;
        String approved = "false";
        item.setOwnerId(5L);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getApprovedBooking(bookingId, ownerId, approved));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getApprovedBooking_whenBookingStatusNotWaiting_thenNotAvailableException() {
        long ownerId = 1L;
        long bookingId = 1L;
        String approved = "false";
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotAvailableException.class,
                () -> bookingService.getApprovedBooking(bookingId, ownerId, approved));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getApprovedBooking_whenApprovedNotTrueOrFalse_thenInvalidValidationException() {
        long ownerId = 1L;
        long bookingId = 1L;
        String approved = "any";
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(InvalidValidationException.class,
                () -> bookingService.getApprovedBooking(bookingId, ownerId, approved));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getApprovedBooking_whenApprovedTrue_thenBookingStatusIsApproved() {
        long ownerId = 1L;
        long bookingId = 1L;
        String approved = "true";
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when((bookingRepository.save(booking))).thenReturn(booking);

        BookingDtoFullOut result = bookingService.getApprovedBooking(bookingId, ownerId, approved);

        assertEquals(BookingStatus.APPROVED.toString(), result.getStatus());
    }

    @Test
    void getApprovedBooking_whenApprovedFalse_thenBookingStatusIsRejected() {
        long ownerId = 1L;
        long bookingId = 1L;
        String approved = "false";
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when((bookingRepository.save(booking))).thenReturn(booking);

        BookingDtoFullOut result = bookingService.getApprovedBooking(bookingId, ownerId, approved);

        assertEquals(BookingStatus.REJECTED.toString(), result.getStatus());
    }

}