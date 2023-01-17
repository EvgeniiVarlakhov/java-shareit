package ru.practicum.shareit.item.dto;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
public class CommentDtoIn {
    private String text;
}
