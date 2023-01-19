package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class BookingDtoIn {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private long bookerId;
    private String status;
}
