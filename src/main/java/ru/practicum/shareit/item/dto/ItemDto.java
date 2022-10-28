package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.CreateItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@RequiredArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(message = "Name не может быть пустым.", groups = {CreateItem.class})
    private String name;
    @NotBlank(message = "Описание не может быть пустым.", groups = {CreateItem.class})
    private String description;
    @NotNull(message = "Следует указать доступность вещи - true/false.", groups = {CreateItem.class})
    private Boolean available;
    private long ownerId;
    private long requestId;
}
