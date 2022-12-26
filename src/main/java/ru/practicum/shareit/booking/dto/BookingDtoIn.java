package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class BookingDtoIn {
    private long id;

    @NotNull(message = "Следует указать дату начала бронирования.")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "Следует указать дату конца бронирования.")
    @FutureOrPresent
    private LocalDateTime end;

    @NotNull(message = "Следует указать id.")
    private long itemId;
    private long bookerId;
    private String status;
}
