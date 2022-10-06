package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@ToString
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long ownerId;
    private ItemRequest request;
}
