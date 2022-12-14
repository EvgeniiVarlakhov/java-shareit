package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Collection<ItemDtoForOwner> getAllItems(long ownerId) {
        validateUser(ownerId);
        Optional<Booking> lastBooking;
        Optional<Booking> nextBooking;
        Collection<ItemDtoForOwner> itemDtoForOwnersList = new ArrayList<>();
        Collection<Item> itemsList = itemRepository.findAllByOwnerIdIsOrderById(ownerId);
        for (Item item : itemsList) {
            Collection<Comment> commentList = commentRepository.findAllByItemIdIs(item.getId());
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
            throw new ObjectNotFoundException("???????? ?? ID = " + itemId + " ???? ????????????????????.");
        }
        Collection<Comment> commentList = commentRepository.findAllByItemIdIs(itemId);
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
        log.info("?????????????? ?????????? ???????? = {}", newItem);
        return ItemMapper.toItemDto(newItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        Item updateItem;
        Optional<Item> itemFromDb = itemRepository.findById(itemId);
        validateUser(userId);
        if (itemFromDb.isEmpty()) {
            throw new ObjectNotFoundException("???????? ?? ?????????? ID = " + itemId + " ???? ????????????????????.");
        }
        if (itemFromDb.get().getOwnerId() != userId) {
            throw new ObjectNotFoundException("?? ???????????????????????? ?? ID = " + userId + " ?????? ???????? ?? ID = " + itemId + ".");
        }
        updateItem = itemRepository.save(ItemMapper.mapUpdateItemFromItemDto(itemFromDb.get(), itemDto));
        log.info("???????? ID = {} ?????????????? ??????????????????. {}", itemId, updateItem);
        return ItemMapper.toItemDto(updateItem);
    }

    @Override
    public Collection<ItemDtoForBooker> searchItemByName(long userId, String text) {
        validateUser(userId);
        Collection<Item> itemsList = new ArrayList<>();
        Collection<CommentDtoOut> commentDtoOutList = new ArrayList<>();
        Collection<ItemDtoForBooker> itemDtoForBookersList = new ArrayList<>();
        if (!text.isBlank()) {
            itemsList.addAll(itemRepository.findAllItemByText(text));
        }
        for (Item item : itemsList) {
            Collection<Comment> comments = commentRepository.findAllByItemIdIs(item.getId());
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
            throw new ObjectNotFoundException("???????? ?? ?????????? ID = " + itemId + " ???? ????????????????????.");
        }
        itemRepository.deleteById(itemId);
        log.info("???????? ?? ID = {} ?????????????? ??????????????.", itemId);
    }

    @Transactional
    @Override
    public CommentDtoOut createComment(long itemId, long userId, CommentDtoIn commentDtoIn) {

        User author = validateUser(userId);
        Optional<Item> itemFromDb = itemRepository.findById(itemId);
        if (itemFromDb.isEmpty()) {
            throw new ObjectNotFoundException("???????? ?? ?????????? ID = " + itemId + " ???? ????????????????????.");
        }
        Optional<Booking> booking = bookingRepository.findBookingByItemIdAndBookerIdAndEndIsBefore(
                itemId,
                userId,
                LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new InvalidValidationException("?????????????????????? ?????????? ???????????????? ???????????? ????????????????????????," +
                    " ?????????????? ???????? ????????, ?????????? ???????????????????? ????????????");
        }
        Comment newComment = new Comment(0, commentDtoIn.getText(), itemId, userId, LocalDateTime.now());
        Comment saveComment = commentRepository.save(newComment);
        log.info("???????????????? ?????????? ?????????????????????? = {}", saveComment);
        return ItemMapper.toCommentDt0FromComment(saveComment, author);
    }

    private User validateUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("???????????????????????? ?? ID = " + userId + " ???? ????????????????????.");
        }
        return user.get();
    }

}
