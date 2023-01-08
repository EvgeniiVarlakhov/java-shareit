package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private Long requestId;
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
