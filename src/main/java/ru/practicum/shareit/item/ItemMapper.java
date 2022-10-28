package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooker;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId()
        );
    }

    public static ItemDtoForBooker toItemDtoForBooker(Item item, Collection<CommentDtoOut> comments) {
        ItemDtoForBooker itemDtoForBooker = new ItemDtoForBooker();
        itemDtoForBooker.setId(item.getId());
        itemDtoForBooker.setName(item.getName());
        itemDtoForBooker.setDescription(item.getDescription());
        itemDtoForBooker.setAvailable(item.getAvailable());
        itemDtoForBooker.setOwnerId(item.getOwnerId());
        itemDtoForBooker.setRequestId(item.getRequestId());
        itemDtoForBooker.setComments(comments);
        return itemDtoForBooker;
    }

    public static ItemDtoForOwner toItemDtoForOwner(Item item, Booking last, Booking next, Collection<CommentDtoOut> comments) {
        ItemDtoForOwner itemDtoForOwner = new ItemDtoForOwner();
        itemDtoForOwner.setId(item.getId());
        itemDtoForOwner.setName(item.getName());
        itemDtoForOwner.setDescription(item.getDescription());
        itemDtoForOwner.setAvailable(item.getAvailable());
        itemDtoForOwner.setOwnerId(item.getOwnerId());
        itemDtoForOwner.setRequestId(item.getRequestId());
        itemDtoForOwner.setComments(comments);
        if (last != null) {
            itemDtoForOwner.setLastBooking(
                    new ItemDtoForOwner.BookingInfo(
                            last.getId(),
                            last.getBookerId(),
                            last.getStart(),
                            last.getEnd()
                    )
            );
        }
        if (next != null) {
            itemDtoForOwner.setNextBooking(
                    new ItemDtoForOwner.BookingInfo(
                            next.getId(),
                            next.getBookerId(),
                            next.getStart(),
                            next.getEnd()
                    )
            );
        }
        return itemDtoForOwner;
    }

    public static CommentDtoOut toCommentDt0FromComment(Comment comment, User author) {
        return new CommentDtoOut(
                comment.getId(),
                comment.getText(),
                author.getName(),
                comment.getCreatedTime()
        );
    }

    public static Item fromItemDto(long userId, ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId,
                itemDto.getRequestId());
    }

    public static Item mapUpdateItemFromItemDto(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

}

