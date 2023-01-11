package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidValidationException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private User owner;
    private User user;
    private Item item;
    private Comment comment;
    private Booking bookingLast;
    private Booking bookingNext;
    private ItemDto itemDto;
    private CommentDtoIn commentDtoIn;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void createUnits() {
        owner = new User(1L, "owner", "owner@ru");
        user = new User(2L, "user", "user@ru");
        item = new Item(1L, "item", "desc", true, 1L, null);
        comment = new Comment(1L, "text", 1L, 2L, LocalDateTime.now());
        bookingLast = new Booking();
        bookingNext = new Booking();
        itemDto = new ItemDto(1L, "item", "desc", true, 1L, null);
        commentDtoIn = new CommentDtoIn("comment");
    }

    @Test
    void getAllItems_whenUserNotFound_thenObjectNotFoundException() {
        long ownerId = 1L;
        int start = 0;
        int size = 10;
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.getAllItems(ownerId, start, size));
    }

    @Test
    void getAllItems_whenWithoutBookings_thenReturnItemWithLastAndNextBookingsAreNull() {
        long ownerId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdIsOrderById(ownerId, pageable)).thenReturn(new PageImpl<>(List.of(item)));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findLastBookingByItem(eq(item.getId()), any())).thenReturn(Optional.empty());
        when(bookingRepository.findNextBookingByItem(eq(item.getId()), any())).thenReturn(Optional.empty());

        Collection<ItemDtoForOwner> result = itemService.getAllItems(ownerId, start, size);

        assertEquals(1, result.size());
        assertNull(new ArrayList<>(result).get(0).getLastBooking());
        assertNull(new ArrayList<>(result).get(0).getNextBooking());
    }

    @Test
    void getAllItems_whenWithoutNextBooking_thenReturnItemWithNextBookingIsNull() {
        long ownerId = 1L;
        int start = 0;
        int size = 10;
        bookingLast.setId(20L);
        Pageable pageable = PageRequest.of(start / size, size);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdIsOrderById(ownerId, pageable)).thenReturn(new PageImpl<>(List.of(item)));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findLastBookingByItem(eq(item.getId()), any())).thenReturn(Optional.of(bookingLast));
        when(bookingRepository.findNextBookingByItem(eq(item.getId()), any())).thenReturn(Optional.empty());

        Collection<ItemDtoForOwner> result = itemService.getAllItems(ownerId, start, size);

        assertEquals(1, result.size());
        assertEquals(bookingLast.getId(), new ArrayList<>(result).get(0).getLastBooking().getId());
        assertNull(new ArrayList<>(result).get(0).getNextBooking());
    }

    @Test
    void getAllItems_whenWithoutLastBooking_thenReturnItemWithLastBookingIsNull() {
        long ownerId = 1L;
        int start = 0;
        int size = 10;
        bookingNext.setId(20L);
        Pageable pageable = PageRequest.of(start / size, size);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdIsOrderById(ownerId, pageable)).thenReturn(new PageImpl<>(List.of(item)));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findLastBookingByItem(eq(item.getId()), any())).thenReturn(Optional.empty());
        when(bookingRepository.findNextBookingByItem(eq(item.getId()), any())).thenReturn(Optional.of(bookingNext));

        Collection<ItemDtoForOwner> result = itemService.getAllItems(ownerId, start, size);

        assertEquals(1, result.size());
        assertEquals(bookingNext.getId(), new ArrayList<>(result).get(0).getNextBooking().getId());
        assertNull(new ArrayList<>(result).get(0).getLastBooking());
    }

    @Test
    void getAllItems_whenWithBookings_thenReturnItemWithBookings() {
        long ownerId = 1L;
        int start = 0;
        int size = 10;
        bookingLast.setId(10L);
        bookingNext.setId(20L);
        Pageable pageable = PageRequest.of(start / size, size);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerIdIsOrderById(ownerId, pageable)).thenReturn(new PageImpl<>(List.of(item)));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findLastBookingByItem(eq(item.getId()), any())).thenReturn(Optional.of(bookingLast));
        when(bookingRepository.findNextBookingByItem(eq(item.getId()), any())).thenReturn(Optional.of(bookingNext));

        Collection<ItemDtoForOwner> result = itemService.getAllItems(ownerId, start, size);

        assertEquals(1, result.size());
        assertEquals(bookingLast.getId(), new ArrayList<>(result).get(0).getLastBooking().getId());
        assertEquals(bookingNext.getId(), new ArrayList<>(result).get(0).getNextBooking().getId());
    }

    @Test
    void getItemById_whenUserNotFound_thenObjectNotFoundException() {
        long userId = 1L;
        long itemId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.getItemById(userId, itemId));
    }

    @Test
    void getItemById_whenItemNotFound_thenObjectNotFoundException() {
        long userId = 1L;
        long itemId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.getItemById(userId, itemId));
    }

    @Test
    void getItemById_whenWithoutBookingsForOwner_thenReturnItemWithLastAndNextBookingsAreNull() {
        long userId = 1L;
        long itemId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(comment));
        when(bookingRepository.findLastBookingByItem(eq(item.getId()), any())).thenReturn(Optional.empty());
        when(bookingRepository.findNextBookingByItem(eq(item.getId()), any())).thenReturn(Optional.empty());

        ItemDtoAbstract result = itemService.getItemById(userId, itemId);

        assertEquals(ItemDtoForOwner.class, result.getClass());
        assertTrue(result.toString().contains("lastBooking=null"));
        assertTrue(result.toString().contains("nextBooking=null"));
    }

    @Test
    void getItemById_whenWithoutBookingsForUser_thenReturnItemWithLastAndNextBookingsAreNull() {
        long userId = 2L;
        long itemId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(comment));
        when(bookingRepository.findLastBookingByItem(eq(item.getId()), any())).thenReturn(Optional.empty());
        when(bookingRepository.findNextBookingByItem(eq(item.getId()), any())).thenReturn(Optional.empty());

        ItemDtoAbstract result = itemService.getItemById(userId, itemId);

        assertEquals(ItemDtoForBooker.class, result.getClass());
        assertTrue(result.toString().contains("lastBooking=null"));
        assertTrue(result.toString().contains("nextBooking=null"));
    }

    @Test
    void getItemById_whenWithBookingsForUser_thenReturnItemWithLastAndNextBookingsAreNull() {
        long userId = 2L;
        long itemId = 1L;
        bookingLast.setId(10L);
        bookingNext.setId(20L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(comment));
        when(bookingRepository.findLastBookingByItem(eq(item.getId()), any())).thenReturn(Optional.of(bookingLast));
        when(bookingRepository.findNextBookingByItem(eq(item.getId()), any())).thenReturn(Optional.of(bookingNext));

        ItemDtoAbstract result = itemService.getItemById(userId, itemId);

        assertEquals(ItemDtoForBooker.class, result.getClass());
        assertTrue(result.toString().contains("lastBooking=null"));
        assertTrue(result.toString().contains("nextBooking=null"));
    }

    @Test
    void getItemById_whenWithBookingsForOwner_thenReturnItemWithLastAndNextBookings() {
        long userId = 1L;
        long itemId = 1L;
        bookingLast.setId(10L);
        bookingNext.setId(20L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(List.of(comment));
        when(bookingRepository.findLastBookingByItem(eq(item.getId()), any())).thenReturn(Optional.of(bookingLast));
        when(bookingRepository.findNextBookingByItem(eq(item.getId()), any())).thenReturn(Optional.of(bookingNext));

        ItemDtoAbstract result = itemService.getItemById(userId, itemId);

        assertEquals(ItemDtoForOwner.class, result.getClass());
        assertFalse(result.toString().contains("lastBooking=null"));
        assertFalse(result.toString().contains("nextBooking=null"));
    }

    @Test
    void createItem_whenUserNotFound_thenObjectNotFoundException() {
        long ownerId = 1L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.createItem(ownerId, itemDto));
    }

    @Test
    void createItem_whenUserFound_thenReturnItemDto() {
        long ownerId = 1L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto result = itemService.createItem(ownerId, itemDto);

        assertEquals(itemDto, result);
    }

    @Test
    void updateItem_whenUserNotFound_thenObjectNotFoundException() {
        long ownerId = 1L;
        long itemId = 1L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(itemId, ownerId, itemDto));
    }

    @Test
    void updateItem_whenItemNotFound_thenObjectNotFoundException() {
        long ownerId = 1L;
        long itemId = 1L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(itemId, ownerId, itemDto));
    }

    @Test
    void updateItem_whenUserDontHaveItem_thenObjectNotFoundException() {
        long ownerId = 1L;
        long itemId = 1L;
        item.setOwnerId(5L);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(itemId, ownerId, itemDto));
    }

    @Test
    void updateItem_whenUserHaveItem_thenReturnUpdateItem() {
        long ownerId = 1L;
        long itemId = 1L;
        itemDto.setName("updateName");
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto result = itemService.updateItem(itemId, ownerId, itemDto);

        assertEquals(itemDto.getName(), result.getName());
    }

    @Test
    void searchItemByName_whenUserNotFound_thenObjectNotFoundException() {
        long userId = 2L;
        String text = " text";
        int start = 0;
        int size = 10;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.searchItemByName(userId, text, start, size));
    }

    @Test
    void searchItemByName_whenTextIsBlank_thenReturnEmptyList() {
        long userId = 2L;
        String text = " ";
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Collection<ItemDtoForBooker> result = itemService.searchItemByName(userId, text, start, size);

        verify(itemRepository, never()).findAllItemByText(text, pageable);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItemByName_whenText_thenReturnItemDto() {
        long userId = 2L;
        String text = "item111";
        int start = 0;
        int size = 10;
        item.setName("item111");
        Pageable pageable = PageRequest.of(start / size, size);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllItemByText(text, pageable)).thenReturn(new PageImpl<>(List.of(item)));

        Collection<ItemDtoForBooker> result = itemService.searchItemByName(userId, text, start, size);

        assertEquals(1, result.size());
        assertEquals(item.getName(), new ArrayList<>(result).get(0).getName());
    }

    @Test
    void deleteItem_whenUserNotFound_thenObjectNotFoundException() {
        long ownerId = 1L;
        long itemId = 1L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.deleteItem(itemId, ownerId));
        verify(itemRepository, never()).deleteById(itemId);
    }

    @Test
    void deleteItem_whenItemNotFound_thenObjectNotFoundException() {
        long ownerId = 1L;
        long itemId = 1L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.deleteItem(itemId, ownerId));
        verify(itemRepository, never()).deleteById(itemId);
    }

    @Test
    void deleteItem_whenItemFound_thenItemDeleteInvoke() {
        long ownerId = 1L;
        long itemId = 1L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(itemId, ownerId);

        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void createComment_whenUserNotFound_thenObjectNotFoundException() {
        long userId = 2L;
        long itemId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.createComment(itemId, userId, commentDtoIn));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenItemNotFound_thenObjectNotFoundException() {
        long userId = 2L;
        long itemId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemService.createComment(itemId, userId, commentDtoIn));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenCommentFromUserWithoutBooking_thenInvalidValidationException() {
        long userId = 2L;
        long itemId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItemIdAndBookerIdAndEndIsBefore(
                eq(itemId),
                eq(userId),
                any()))
                .thenReturn(Optional.empty());

        assertThrows(InvalidValidationException.class, () -> itemService.createComment(itemId, userId, commentDtoIn));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenValidateIsOk_thenReturnCommentDtoOut() {
        long userId = 2L;
        long itemId = 1L;
        Comment newComment = comment;
        newComment.setText(commentDtoIn.getText());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItemIdAndBookerIdAndEndIsBefore(eq(itemId), eq(userId), any()))
                .thenReturn(Optional.of(new Booking()));
        when(commentRepository.save(any(Comment.class))).thenReturn(newComment);

        CommentDtoOut result = itemService.createComment(itemId, userId, commentDtoIn);

        assertEquals(commentDtoIn.getText(), result.getText());
    }

}