package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidValidationException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAbstract;
import ru.practicum.shareit.item.dto.ItemDtoForBooker;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
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
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Collection<ItemDtoForOwner> getAllItems(long ownerId, int start, int size) {
        validateUser(ownerId);
        Pageable pageable = PageRequest.of(start / size, size);
        Optional<Booking> lastBooking;
        Optional<Booking> nextBooking;
        Collection<ItemDtoForOwner> itemDtoForOwnersList = new ArrayList<>();
        Collection<Item> itemsList = itemRepository.findAllByOwnerIdIsOrderById(ownerId, pageable).getContent();
        for (Item item : itemsList) {
            Collection<Comment> commentList = commentRepository.findAllByItemId(item.getId());
            Collection<CommentDtoOut> commentDtoOutList = new ArrayList<>();
            for (Comment comment : commentList) {
                User author = validateUser(comment.getAuthorID());
                commentDtoOutList.add(ItemMapper.toCommentDt0FromComment(comment, author));
            }
            lastBooking = bookingRepository.findLastBookingByItem(item.getId(), LocalDateTime.now());
            nextBooking = bookingRepository.findNextBookingByItem(item.getId(), LocalDateTime.now());
            Booking last;
            Booking next;
            if (lastBooking.isEmpty()) {
                last = null;
            } else {
                last = lastBooking.get();
            }
            if (nextBooking.isEmpty()) {
                next = null;
            } else {
                next = nextBooking.get();
            }
            itemDtoForOwnersList.add(ItemMapper.toItemDtoForOwner(item, last, next, commentDtoOutList));
        }
        return itemDtoForOwnersList;
    }

    @Override
    public ItemDtoAbstract getItemById(long userId, long itemId) {
        validateUser(userId);
        Optional<Item> itemFromDb = itemRepository.findById(itemId);
        if (itemFromDb.isEmpty()) {
            throw new ObjectNotFoundException("Вещи с ID = " + itemId + " не существует.");
        }
        Collection<Comment> commentList = commentRepository.findAllByItemId(itemId);
        Collection<CommentDtoOut> commentDtoOutList = new ArrayList<>();
        for (Comment comment : commentList) {
            User author = validateUser(comment.getAuthorID());
            commentDtoOutList.add(ItemMapper.toCommentDt0FromComment(comment, author));
        }
        Optional<Booking> lastBooking = bookingRepository.findLastBookingByItem(itemId, LocalDateTime.now());
        Optional<Booking> nextBooking = bookingRepository.findNextBookingByItem(itemId, LocalDateTime.now());
        Booking last;
        Booking next;
        if (lastBooking.isEmpty()) {
            last = null;
        } else {
            last = lastBooking.get();
        }
        if (nextBooking.isEmpty()) {
            next = null;
        } else {
            next = nextBooking.get();
        }
        if (itemFromDb.get().getOwnerId() == userId) {
            return ItemMapper.toItemDtoForOwner(itemFromDb.get(), last, next, commentDtoOutList);
        } else {
            return ItemMapper.toItemDtoForBooker(itemFromDb.get(), commentDtoOutList);
        }
    }

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        validateUser(userId);
        Item newItem = itemRepository.save(ItemMapper.fromItemDto(userId, itemDto));
        log.info("Создана новая вещь = {}", newItem);
        return ItemMapper.toItemDto(newItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        Item updateItem;
        Optional<Item> itemFromDb = itemRepository.findById(itemId);
        validateUser(userId);
        if (itemFromDb.isEmpty()) {
            throw new ObjectNotFoundException("Вещь с таким ID = " + itemId + " не существует.");
        }
        if (itemFromDb.get().getOwnerId() != userId) {
            throw new ObjectNotFoundException("У пользователя с ID = " + userId + " нет вещи с ID = " + itemId + ".");
        }
        updateItem = itemRepository.save(ItemMapper.mapUpdateItemFromItemDto(itemFromDb.get(), itemDto));
        log.info("Вещь ID = {} успешно обновлена. {}", itemId, updateItem);
        return ItemMapper.toItemDto(updateItem);
    }

    @Override
    public Collection<ItemDtoForBooker> searchItemByName(long userId, String text, int start, int size) {
        validateUser(userId);
        Pageable pageable = PageRequest.of(start / size, size);
        Collection<Item> itemsList = new ArrayList<>();
        Collection<CommentDtoOut> commentDtoOutList = new ArrayList<>();
        Collection<ItemDtoForBooker> itemDtoForBookersList = new ArrayList<>();
        if (!text.isBlank()) {
            itemsList.addAll(itemRepository.findAllItemByText(text, pageable).getContent());
        }
        for (Item item : itemsList) {
            Collection<Comment> comments = commentRepository.findAllByItemId(item.getId());
            for (Comment comment : comments) {
                User author = validateUser(comment.getAuthorID());
                commentDtoOutList.add(ItemMapper.toCommentDt0FromComment(comment, author));
            }
            itemDtoForBookersList.add(ItemMapper.toItemDtoForBooker(item, commentDtoOutList));

        }
        return itemDtoForBookersList;
    }

    @Transactional
    @Override
    public void deleteItem(long itemId, long userId) {
        validateUser(userId);
        Optional<Item> itemFromDb = itemRepository.findById(itemId);
        if (itemFromDb.isEmpty()) {
            throw new ObjectNotFoundException("Вещь с таким ID = " + itemId + " не существует.");
        }
        itemRepository.deleteById(itemId);
        log.info("Вещь с ID = {} успешно удалена.", itemId);
    }

    @Transactional
    @Override
    public CommentDtoOut createComment(long itemId, long userId, CommentDtoIn commentDtoIn) {
        User author = validateUser(userId);
        Optional<Item> itemFromDb = itemRepository.findById(itemId);
        if (itemFromDb.isEmpty()) {
            throw new ObjectNotFoundException("Вещь с таким ID = " + itemId + " не существует.");
        }
        Optional<Booking> booking = bookingRepository.findBookingByItemIdAndBookerIdAndEndIsBefore(
                itemId,
                userId,
                LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new InvalidValidationException("Комментарии может оставить только пользователь," +
                    " который брал вещи, после завершения оренды");
        }
        Comment newComment = new Comment(0, commentDtoIn.getText(), itemId, userId, LocalDateTime.now());
        Comment saveComment = commentRepository.save(newComment);
        log.info("Сохранен новый комментарий = {}", saveComment);
        return ItemMapper.toCommentDt0FromComment(saveComment, author);
    }

    private User validateUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Пользователя с ID = " + userId + " не существует.");
        }
        return user.get();
    }

}
