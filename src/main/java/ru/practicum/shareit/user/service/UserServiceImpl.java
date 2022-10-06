package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final Set<String> emails = new HashSet<>();

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<UserDto> listUsers = new ArrayList<>();
        for (User user : userStorage.getUsersList()) {
            listUsers.add(UserMapper.toUserDto(user));
        }
        return listUsers;
    }

    @Override
    public UserDto createUser(User user) {
        if (!emails.contains(user.getEmail())) {
            emails.add(user.getEmail());
            UserDto newUserDto = UserMapper.toUserDto(userStorage.addUser(user));
            log.info("Создан новый пользователь: {}", newUserDto);
            return newUserDto;
        } else {
            throw new ConflictException("Пользователь с таким Email уже существует.");
        }
    }

    @Override
    public UserDto updateUser(User user, long idUser) {
        if (!emails.contains(user.getEmail())) {
            if (user.getEmail() != null) {
                emails.remove(userStorage.getUserById(idUser).getEmail());
            }
            UserMapper.fromUserDto(user, userStorage.getUserById(idUser));
            UserDto newUserDto = UserMapper.toUserDto(userStorage.getUserById(idUser));
            log.info("Изменен пользователь id = {} : {}", idUser, newUserDto);
            return newUserDto;
        } else {
            throw new ConflictException("Пользователь с таким Email уже существует.");
        }
    }

    @Override
    public void deleteUser(long id) {
        emails.remove(userStorage.getUserById(id).getEmail());
        userStorage.deleteUser(id);
        log.info("Удален пользователь id = {}", id);
    }

    @Override
    public UserDto getUserById(long idUser) {
        return UserMapper.toUserDto(userStorage.getUserById(idUser));
    }

}
