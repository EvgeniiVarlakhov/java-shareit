package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserDto userDto;
    private User userFromDb;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void createdUserDto() {
        userDto = new UserDto(null, "name", "email");
        userFromDb = new User(1L, "name", "email");
    }

    @Test
    void getAllUsers_whenInvoked_returnUsersList() {
        List<User> userListFromDb = List.of(userFromDb);

        when(userRepository.findAll()).thenReturn(userListFromDb);

        Collection<UserDto> responseUsersList = userServiceImpl.getAllUsers();

        verify(userRepository).findAll();
        assertEquals(userListFromDb.size(), responseUsersList.size());
    }

    @Test
    void createUser_whenValidateUserDtoIsOk_thenReturnSaveUser() {
        User userForSave = new User(null, "name", "email");
        when(userRepository.save(userForSave)).thenReturn(userFromDb);

        UserDto returnUserDto = userServiceImpl.createUser(userDto);

        verify(userRepository).save(userForSave);
        assertEquals(userFromDb.getId(), returnUserDto.getId());
        assertEquals(userFromDb.getName(), returnUserDto.getName());
    }

    @Test
    void createUser_whenValidateUserDtoIsBad_thenThrowException() {
        ConflictException conflictException = new ConflictException("Пользователь с таким Email уже существует.");
        User userForSave = new User(null, "name", "email");
        when(userRepository.save(userForSave)).thenThrow(conflictException);

        assertThrows(ConflictException.class, () -> userServiceImpl.createUser(userDto));
    }

    @Test
    void updateUser_whenUserNotFound_thenObjectNotFoundException() {
        long userId = 1L;
        userDto = new UserDto(25L, "newName", "newEmail");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userServiceImpl.updateUser(userDto, userId));

        verify(userRepository, never()).save(new User(1L, "newName", "newEmail"));
    }

    @Test
    void updateUser_whenUserFound_thenUserUpdate() {
        long userId = 1L;
        userDto = new UserDto(25L, "newName", "newEmail");
        UserDto updateUserDto = new UserDto(1L, "newName", "newEmail");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userFromDb));
        when(userRepository.save(any(User.class))).thenReturn(new User(1L, "newName", "newEmail"));

        UserDto actualUserDto = userServiceImpl.updateUser(userDto, userId);

        assertEquals(updateUserDto.getId(), actualUserDto.getId());
        assertEquals(updateUserDto.getName(), actualUserDto.getName());
        assertEquals(updateUserDto.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void deleteUser_whenUserNotFound_thenObjectNotFoundException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userServiceImpl.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void deleteUser_whenUserFound_thenUserRepositoryInvoke() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(userFromDb));

        userServiceImpl.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void getUserById_whenUserNotFound_thenObjectNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userServiceImpl.getUserById(userId));
    }

    @Test
    void getUserById_whenUserFound_thenReturnUser() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(userFromDb));

        UserDto actualUserDto = userServiceImpl.getUserById(userId);

        assertEquals(userFromDb.getId(), actualUserDto.getId());
        assertEquals(userFromDb.getName(), actualUserDto.getName());
        assertEquals(userFromDb.getEmail(), actualUserDto.getEmail());
    }

}