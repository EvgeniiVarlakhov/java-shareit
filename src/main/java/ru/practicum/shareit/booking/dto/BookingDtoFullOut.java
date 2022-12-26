package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class BookingDtoFullOut {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
    private UserInfo booker;
    private ItemInfo item;

    @Data
    public static class UserInfo {
        private final long id;
    }

    @Data
    public static class ItemInfo {
        private final long id;
        private final String name;
    }
}
