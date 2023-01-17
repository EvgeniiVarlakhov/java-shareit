package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
public class ItemRequestDtoIn {

    @NotBlank(message = "Запрос не может быть пустым")
    private String description;
}
