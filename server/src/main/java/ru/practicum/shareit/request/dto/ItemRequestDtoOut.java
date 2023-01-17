package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
public class ItemRequestDtoOut {
    private Long id;
    private String description;
    private LocalDateTime created;
}
