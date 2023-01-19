package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    private final UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getAllUsers_whenDBIsEmpty_thenReturnEmptyList() {

        Collection<UserDto> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    void getAllUsers_whenDBHaveTwoUsers_thenReturnListSizeTwo() {
        User user1 = new User(0L, "user1", "one@ru");
        User user2 = new User(0L, "user2", "two@ru");
        userRepository.save(user1);
        userRepository.save(user2);

        Collection<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(1, new ArrayList<>(result).get(0).getId());
        assertEquals(2, new ArrayList<>(result).get(1).getId());
    }

    @DirtiesContext
    @Test
    void createUser_whenUserWithSameEmail_thenConflictException() {
        User user = new User(0L, "user", "same@ru");
        userRepository.save(user);
        UserDto userDto = new UserDto(0L, "userCreate", "same@ru");

        assertThrows(ConflictException.class, () -> userService.createUser(userDto));
    }

    @DirtiesContext
    @Test
    void createUser_whenDataBaseIsEmpty_thenReturnUserDto() {
        UserDto userDto = new UserDto(0L, "userCreate", "userCreate@ru");

        // База Данных (далее БД) пустая
        assertTrue(userRepository.findAll().isEmpty());

        UserDto result = userService.createUser(userDto);
        ArrayList<User> listOfUsers = new ArrayList<>(userRepository.findAll());

        //Проверили что вернулось из БД
        assertEquals(1, listOfUsers.size());
        assertEquals(userDto.getName(), listOfUsers.get(0).getName());
        assertEquals(userDto.getEmail(), listOfUsers.get(0).getEmail());

        //Проверили что вернул метод сервиса
        assertEquals(1L, result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @DirtiesContext
    @Test
    void updateUser_whenInvoke_thenReturnUpdateUser() {
        User userOld = new User(0L, "userOld", "old@ru");
        userRepository.save(userOld);
        UserDto userUpdate = new UserDto(0L, "userUpdate", "userUpdate@ru");

        UserDto result = userService.updateUser(userUpdate, 1L);

        assertEquals(1L, result.getId());
        assertEquals(userUpdate.getName(), result.getName());
        assertEquals(userUpdate.getEmail(), result.getEmail());
    }

    @DirtiesContext
    @Test
    void deleteUser_whenInvoke_thenBdIsEmpty() {
        User userOld = new User(0L, "userOld", "old@ru");

        // Заполняем БД
        userRepository.save(userOld);

        //Проверяем что в БД добавлен пользователь
        assertEquals(1, userRepository.findAll().size());

        //Удаляем пользователя из БД методом сервиса
        userService.deleteUser(1L);

        //Проверяем что БД пустая
        assertTrue(userRepository.findAll().isEmpty());
    }

    @DirtiesContext
    @Test
    void getUserById_whenGetUserID_thenReturnCorrectUser() {
        User userOld = new User(0L, "userOld", "old@ru");
        User user = new User(0L, "user", "user@ru");

        //Заполняем БД
        userRepository.save(userOld);
        userRepository.save(user);

        UserDto userDto1 = userService.getUserById(1L);
        UserDto userDto2 = userService.getUserById(2L);

        assertEquals(1L, userDto1.getId());
        assertEquals(userOld.getName(), userDto1.getName());
        assertEquals(userOld.getEmail(), userDto1.getEmail());
        assertEquals(2L, userDto2.getId());
        assertEquals(user.getName(), userDto2.getName());
        assertEquals(user.getEmail(), userDto2.getEmail());
    }

}