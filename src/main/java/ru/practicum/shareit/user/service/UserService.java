package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> getAllUsers();

    UserDto createUser(User user);

    UserDto updateUser(User user, long idUser);

    void deleteUser(long id);

    UserDto getUserById(long idUser);

}
