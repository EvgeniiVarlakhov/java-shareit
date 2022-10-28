package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;

@ToString
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoForOwner extends ItemDtoAbstract {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long ownerId;
    private long requestId;
    private BookingInfo lastBooking;
    private BookingInfo nextBooking;
    private Collection<CommentDtoOut> comments;

    @ToString
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingInfo {
        private long id;
        private long bookerId;
        private LocalDateTime start;
        private LocalDateTime end;
    }

}
