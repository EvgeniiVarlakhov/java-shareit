package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private UserDto userDto;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void createdUser() {
        userDto = new UserDto(1L, "name", "email");
    }

    @Test
    void getAllUsers_whenInvoked_thenUsersCollectionReturn() {
        Collection<UserDto> expectedUsers = List.of(userDto);
        Mockito.when((userService.getAllUsers())).thenReturn(expectedUsers);

        Collection<UserDto> responseUsers = userController.getAllUsers();

        assertEquals(1, responseUsers.size());
    }

    @Test
    void getUserById_whenInvoked_thenReturnUser() {

        Mockito.when(userService.getUserById(userDto.getId())).thenReturn(userDto);

        UserDto responseUserDto = userController.getUserById(1L);

        assertEquals(userDto.getId(), responseUserDto.getId());
    }

    @Test
    void createUser_whenInvoked_thenReturnUser() {

        Mockito.when(userService.createUser(userDto)).thenReturn(userDto);

        UserDto responseUserDto = userController.createUser(userDto);

        assertEquals(userDto.getId(), responseUserDto.getId());
    }

    @Test
    void updateUser_whenInvoked_thenReturnUser() {
        long userId = 1L;
        Mockito.when(userService.updateUser(userDto, userId)).thenReturn(userDto);

        UserDto responseUserDto = userController.updateUser(userDto, userId);

        assertEquals(userDto.getId(), responseUserDto.getId());
    }

}