package ru.practicum.shareit.item.dto;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long ownerId;
    private Long requestId;
}
