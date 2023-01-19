package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class BookingDtoIn {
    @Positive
    private Long id;

    @NotNull(message = "Следует указать дату начала бронирования.")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "Следует указать дату конца бронирования.")
    @FutureOrPresent
    private LocalDateTime end;

    @NotNull(message = "Следует указать id.")
    private Long itemId;
    private long bookerId;
    private String status;
}
