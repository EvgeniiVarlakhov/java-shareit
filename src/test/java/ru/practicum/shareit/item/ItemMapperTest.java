package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooker;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto_whenInvoke_thenReturnItemDto() {
        Item item = new Item(1L, "name", "desc", true, 1L, 2L);

        ItemDto result = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getRequestId(), result.getRequestId());
    }

    @Test
    void toItemDtoForBooker_whenInvoke_theReturnItemDtoBooker() {
        Item item = new Item(1L, "name", "desc", true, 1L, 2L);
        Collection<CommentDtoOut> comments = List.of(new CommentDtoOut());

        ItemDtoForBooker result = ItemMapper.toItemDtoForBooker(item, comments);

        assertEquals(item.getId(), result.getId());
        assertEquals(comments.size(), result.getComments().size());
    }

    @Test
    void toItemDtoForOwner_whenBookingsIsNull_thenReturnItemDtoOwnerWithBothBookingNull() {
        Item item = new Item(1L, "name", "desc", true, 1L, 2L);
        Collection<CommentDtoOut> comments = List.of(new CommentDtoOut());

        ItemDtoForOwner result = ItemMapper.toItemDtoForOwner(item, null, null, comments);

        assertEquals(item.getId(), result.getId());
        assertNull(result.getNextBooking());
        assertNull(result.getLastBooking());
    }

    @Test
    void toItemDtoForOwner_whenLastBookingIsNull_thenReturnItemDtoOwnerWithLastBookingNull() {
        Item item = new Item(1L, "name", "desc", true, 1L, 2L);
        Collection<CommentDtoOut> comments = List.of(new CommentDtoOut());

        ItemDtoForOwner result = ItemMapper.toItemDtoForOwner(item, null, new Booking(), comments);

        assertEquals(item.getId(), result.getId());
        assertNotNull(result.getNextBooking());
        assertNull(result.getLastBooking());
    }

    @Test
    void toItemDtoForOwner_whenNextBookingIsNull_thenReturnItemDtoOwnerWithNextBookingNull() {
        Item item = new Item(1L, "name", "desc", true, 1L, 2L);
        Collection<CommentDtoOut> comments = List.of(new CommentDtoOut());

        ItemDtoForOwner result = ItemMapper.toItemDtoForOwner(item, new Booking(), null, comments);

        assertEquals(item.getId(), result.getId());
        assertNull(result.getNextBooking());
        assertNotNull(result.getLastBooking());
    }

    @Test
    void toItemDtoForOwner_whenBookingsIsNotNull_thenReturnItemDtoOwnerWithBookings() {
        Item item = new Item(1L, "name", "desc", true, 1L, 2L);
        Collection<CommentDtoOut> comments = List.of(new CommentDtoOut());

        ItemDtoForOwner result = ItemMapper.toItemDtoForOwner(item, new Booking(), new Booking(), comments);

        assertEquals(item.getId(), result.getId());
        assertNotNull(result.getNextBooking());
        assertNotNull(result.getLastBooking());
    }

    @Test
    void toCommentDt0FromComment_whenInvoke_thenReturnCommentDtoOut() {
        Comment newComment = new Comment(1L, "text", 1L, 1L, LocalDateTime.now());
        User author = new User(1L, "name", "email@ru");

        CommentDtoOut result = ItemMapper.toCommentDt0FromComment(newComment, author);

        assertEquals(author.getName(), result.getAuthorName());
        assertEquals(newComment.getCreatedTime(), result.getCreated());
    }

    @Test
    void fromItemDto_whenInvoke_thenReturnItem() {
        long userId = 1L;
        ItemDto newItemDto = new ItemDto(1L, "name", "desc", true, 10L, 2L);

        Item result = ItemMapper.fromItemDto(userId, newItemDto);

        assertEquals(newItemDto.getId(), result.getId());
        assertEquals(userId, result.getOwnerId());
    }

    @Test
    void mapUpdateItemFromItemDto_whenAllFieldsIsNull_thenReturnNotUpdateItem() {
        Item oldItem = new Item(1L, "oldName", "oldDesc", true, 1L, 2L);
        ItemDto updateItem = new ItemDto(1L, null, null, null, 1L, 1L);

        Item result = ItemMapper.mapUpdateItemFromItemDto(oldItem, updateItem);

        assertEquals(oldItem.getName(), result.getName());
        assertEquals(oldItem.getDescription(), result.getDescription());
        assertEquals(oldItem.getAvailable(), result.getAvailable());
    }

    @Test
    void mapUpdateItemFromItemDto_whenNameAllFieldIsNotNull_thenReturnUpdateItem() {
        Item oldItem = new Item(1L, "oldName", "oldDesc", true, 1L, 2L);
        ItemDto updateItem = new ItemDto(1L, "new", "new", false, 1L, 1L);

        Item result = ItemMapper.mapUpdateItemFromItemDto(oldItem, updateItem);

        assertEquals(updateItem.getName(), result.getName());
        assertEquals(updateItem.getDescription(), result.getDescription());
        assertEquals(updateItem.getAvailable(), result.getAvailable());
    }

}