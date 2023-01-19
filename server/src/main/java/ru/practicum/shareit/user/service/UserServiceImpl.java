package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<User> users = userRepository.findAll();
        log.info("Получен список пользователей.{}", users);
        return UserMapper.mapListToUserDto(users);
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User newSaveUser = validateUserDto(userDto);
        log.info("Создан новый пользователь = {}", newSaveUser);
        return UserMapper.toUserDto(newSaveUser);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, long idUser) {
        User updateUser;
        Optional<User> userFromDb = userRepository.findById(idUser);
        if (userFromDb.isEmpty()) {
            throw new ObjectNotFoundException("Пользователя с ID = " + idUser + " не существует.");
        }
        updateUser = userRepository.save(UserMapper.mapUpdateFromUserDto(userDto, userFromDb.get()));
        log.info("Изменен пользователь id = {} : {}", idUser, updateUser);
        return UserMapper.toUserDto(updateUser);
    }

    @Transactional
    @Override
    public void deleteUser(long idUser) {
        Optional<User> userFromDb = userRepository.findById(idUser);
        if (userFromDb.isEmpty()) {
            throw new ObjectNotFoundException("Пользователя с ID = " + idUser + " не существует.");
        }
        userRepository.deleteById(idUser);
        log.info("Пользователь ID = {} успешно удален.", idUser);
    }

    @Override
    public UserDto getUserById(long idUser) {
        Optional<User> userFromDb = userRepository.findById(idUser);
        if (userFromDb.isEmpty()) {
            throw new ObjectNotFoundException("Пользователя с ID = " + idUser + " не существует.");
        }
        return UserMapper.toUserDto(userFromDb.get());
    }

    private User validateUserDto(UserDto userDto) {
        User newUser;
        try {
            newUser = userRepository.save(UserMapper.mapToNewUser(userDto));
        } catch (Exception e) {
            throw new ConflictException("Пользователь с таким Email уже существует.");
        }
        return newUser;
    }

}
