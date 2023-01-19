package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoFullOut;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidValidationException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoFullOut getBookingInfo(long userId, long bookingId) {
        validateUser(userId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new ObjectNotFoundException("Бронирования с ID = " + bookingId + " не существует.");
        }
        Optional<Item> item = itemRepository.findById(booking.get().getItemId());
        if (item.isEmpty()) {
            throw new ObjectNotFoundException("Вещи c ID = " + booking.get().getItemId() + " не существоет.");
        }
        if (!(booking.get().getBookerId() == userId || item.get().getOwnerId() == userId)) {
            throw new ObjectNotFoundException("Проверить бронирование может только владелец вещи или бронирующий.");
        }
        User owner = validateUser(booking.get().getBookerId());
        log.info("Получена информация для бронированя с ID = {} для пользователя с ID = {}", booking, userId);
        return BookingMapper.mapToBookingFullOut(booking.get(), owner, item.get());
    }

    @Override
    public Collection<BookingDtoFullOut> getListOfBookingsBooker(long bookerId, String state, int start, int size) {
        Pageable pageable = PageRequest.of(start / size, size);
        BookingState bookingState = BookingState.valueOf(state);
        User booker = validateUser(bookerId);
        Collection<BookingDtoFullOut> listOfBookingReturn = new ArrayList<>();
        Collection<Booking> listOfBooking = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                listOfBooking = bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable).getContent();
                break;
            case CURRENT:
                listOfBooking = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        bookerId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        pageable).getContent();
                break;
            case PAST:
                listOfBooking = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                        bookerId,
                        LocalDateTime.now(),
                        pageable).getContent();
                break;
            case FUTURE:
                listOfBooking = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                        bookerId,
                        LocalDateTime.now(),
                        pageable).getContent();
                break;
            case WAITING:
                listOfBooking = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                        bookerId,
                        BookingStatus.WAITING,
                        pageable).getContent();
                break;
            case REJECTED:
                listOfBooking = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(
                        bookerId,
                        BookingStatus.REJECTED,
                        pageable).getContent();
                break;
        }
        for (Booking booking : listOfBooking) {
            Optional<Item> item = itemRepository.findById(booking.getItemId());
            item.ifPresent(value -> listOfBookingReturn.add(BookingMapper.mapToBookingFullOut(booking, booker, value)));
        }
        log.info("Получен список бронирований для пользователя с ID = {}. Список = {}.", bookerId, listOfBookingReturn);
        return listOfBookingReturn;
    }

    @Override
    public Collection<BookingDtoFullOut> getListOfBookingsOwner(long ownerId, String state, int start, int size) {
        Pageable pageable = PageRequest.of(start / size, size);
        BookingState bookingState = BookingState.valueOf(state);
        validateUser(ownerId);
        Collection<Item> itemsListByOwner = itemRepository.findAllByOwnerIdIsOrderById(
                        ownerId,
                        PageRequest.of(0, 1))
                .getContent();
        if (itemsListByOwner.isEmpty()) {
            throw new ObjectNotFoundException("У пользователя ID = " + ownerId + " нет ни одной вещи.");
        }
        Collection<BookingDtoFullOut> listOfBookingReturn = new ArrayList<>();
        Collection<Booking> listOfBooking = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                listOfBooking = bookingRepository.findAllBookingsForOwner(ownerId, pageable).getContent();
                break;
            case CURRENT:
                listOfBooking = bookingRepository.findCurrentBookingsForOwner(
                                ownerId,
                                LocalDateTime.now(),
                                pageable)
                        .getContent();
                break;
            case PAST:
                listOfBooking = bookingRepository.findPastBookingsForOwner(
                                ownerId,
                                LocalDateTime.now(),
                                pageable)
                        .getContent();
                break;
            case FUTURE:
                listOfBooking = bookingRepository.findFutureBookingsForOwner(
                                ownerId,
                                LocalDateTime.now(),
                                pageable)
                        .getContent();
                break;
            case WAITING:
                listOfBooking = bookingRepository.findStatusBookingsForOwner(
                                ownerId,
                                BookingStatus.WAITING.toString(),
                                pageable)
                        .getContent();
                break;
            case REJECTED:
                listOfBooking = bookingRepository.findStatusBookingsForOwner(
                                ownerId,
                                BookingStatus.REJECTED.toString(),
                                pageable)
                        .getContent();
                break;
        }
        for (Booking booking : listOfBooking) {
            Optional<User> booker = userRepository.findById(booking.getBookerId());
            Optional<Item> item = itemRepository.findById(booking.getItemId());
            item.ifPresent(
                    value -> booker.ifPresent(
                            user -> listOfBookingReturn.add(BookingMapper.mapToBookingFullOut(booking, user, value))));
        }
        log.info("Получен список бронирований для пользователя с ID = {}. Список = {}.", ownerId, listOfBookingReturn);
        return listOfBookingReturn;
    }

    @Override
    @Transactional
    public BookingDtoFullOut createBooking(BookingDtoIn bookingDto, long bookerId) {
        User booker = validateUser(bookerId);
        checkBookingParam(bookingDto);
        Item item = checkItem(bookingDto.getItemId());
        if (item.getOwnerId() == bookerId) {
            throw new ObjectNotFoundException("Владелец вещи не может забронировать собственную вещь.");
        }
        Booking newBooking = bookingRepository.save(BookingMapper.mapNewBookingFromDto(bookingDto, bookerId));
        log.info("Добавлено новое бронирование = {}", newBooking);
        return BookingMapper.mapToBookingFullOut(newBooking, booker, item);
    }

    @Override
    @Transactional
    public BookingDtoFullOut getApprovedBooking(long bookingId, long ownerId, String approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new ObjectNotFoundException("Запроса на бронирование с ID = " + bookingId + " не существоет.");
        }
        validateUser(ownerId);
        User booker = validateUser(booking.get().getBookerId());
        Item item = checkItem(booking.get().getItemId());
        if (item.getOwnerId() != ownerId) {
            throw new ObjectNotFoundException("Одобрить бронирование может только владелец вещи");
        }
        if (booking.get().getStatus() != BookingStatus.WAITING) {
            throw new NotAvailableException("Заявка на бронирование уже обработана.");
        }
        switch (approved) {
            case "true":
                booking.get().setStatus(BookingStatus.APPROVED);
                break;
            case "false":
                booking.get().setStatus(BookingStatus.REJECTED);
                break;
            default:
                throw new InvalidValidationException("Значение должно быть true/false");
        }
        Booking updateBooking = bookingRepository.save(booking.get());
        log.info("Обработана заявка на бронирование ID = {} c результатом = {}", bookingId, booking.get().getStatus());
        return BookingMapper.mapToBookingFullOut(updateBooking, booker, item);
    }

    private User validateUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Пользователя с ID = " + userId + " не существует.");
        }
        return user.get();
    }

    private Item checkItem(long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ObjectNotFoundException("Вещи с ID = " + itemId + " не существует.");
        }
        if (!item.get().getAvailable()) {
            throw new NotAvailableException("Вещь с ID = " + itemId + " не доступна для бронирования.");
        }
        return item.get();
    }

    private void checkBookingParam(BookingDtoIn bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new NotAvailableException("Дата окончания бронирования не может быть раньше даты начала.");
        }
    }

}
