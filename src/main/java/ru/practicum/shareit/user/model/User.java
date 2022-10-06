package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.CreateUser;
import ru.practicum.shareit.user.UpdateUser;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class User {
    private long id;
    @NotBlank(message = "Name не может быть пустым.", groups = {CreateUser.class})
    private String name;
    @Email(message = "Email имеет не правельный формат.", groups = {CreateUser.class, UpdateUser.class})
    @NotBlank(message = "Email не может быть пустым.", groups = {CreateUser.class})
    private String email;
}
