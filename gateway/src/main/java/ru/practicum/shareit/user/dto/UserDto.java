package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.user.CreateUser;
import ru.practicum.shareit.user.UpdateUser;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class UserDto {
    @Positive
    private Long id;

    @NotBlank(message = "Name не может быть пустым.", groups = {CreateUser.class})
    private String name;

    @Email(message = "Email имеет неправильный формат.", groups = {CreateUser.class, UpdateUser.class})
    @NotBlank(message = "Email не может быть пустым.", groups = {CreateUser.class})
    private String email;
}
