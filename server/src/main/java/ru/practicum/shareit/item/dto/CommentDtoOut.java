package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
public class CommentDtoOut {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
