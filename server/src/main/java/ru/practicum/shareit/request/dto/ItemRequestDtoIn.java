package ru.practicum.shareit.request.dto;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
public class ItemRequestDtoIn {
    private String description;
}
