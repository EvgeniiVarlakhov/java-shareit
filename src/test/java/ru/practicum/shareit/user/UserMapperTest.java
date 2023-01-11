package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    @Test
    void toUserDto_whenInvokeWithUser_thenReturnUserDto() {
        User user = new User(1L, "name", "email@ru");

        UserDto result = UserMapper.toUserDto(user);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void mapUpdateFromUserDto_whenNameIsNullAndEmailIsNull_thenReturnOldUser() {
        User oldUser = new User(1L, "oldName", "oldEmail@ru");
        UserDto newUserDto = new UserDto(null, null, null);

        User updateUser = UserMapper.mapUpdateFromUserDto(newUserDto, oldUser);

        assertEquals(oldUser.getId(), updateUser.getId());
        assertEquals(oldUser.getName(), updateUser.getName());
        assertEquals(oldUser.getEmail(), updateUser.getEmail());
    }

    @Test
    void mapUpdateFromUserDto_whenEmailIsNull_thenReturnUpdateUsersName() {
        User oldUser = new User(1L, "oldName", "oldEmail@ru");
        UserDto newUserDto = new UserDto(10L, "newName", null);

        User updateUser = UserMapper.mapUpdateFromUserDto(newUserDto, oldUser);

        assertEquals(oldUser.getId(), updateUser.getId());
        assertEquals("newName", updateUser.getName());
        assertEquals(oldUser.getEmail(), updateUser.getEmail());
    }

    @Test
    void mapUpdateFromUserDto_whenNameIsNull_thenReturnUpdateUsersEmail() {
        User oldUser = new User(1L, "oldName", "oldEmail@ru");
        UserDto newUserDto = new UserDto(10L, null, "newEmail@ru");

        User updateUser = UserMapper.mapUpdateFromUserDto(newUserDto, oldUser);

        assertEquals(oldUser.getId(), updateUser.getId());
        assertEquals(oldUser.getName(), updateUser.getName());
        assertEquals("newEmail@ru", updateUser.getEmail());
    }

    @Test
    void mapUpdateFromUserDto_whenNameInvoke_thenReturnUpdateUser() {
        User oldUser = new User(1L, "oldName", "oldEmail@ru");
        UserDto newUserDto = new UserDto(10L, "newName", "newEmail@ru");

        User updateUser = UserMapper.mapUpdateFromUserDto(newUserDto, oldUser);

        assertEquals(oldUser.getId(), updateUser.getId());
        assertEquals("newName", updateUser.getName());
        assertEquals("newEmail@ru", updateUser.getEmail());
    }

    @Test
    void mapToNewUser_whenInvokeWithUserDto_thenReturnUser() {
        UserDto userDto = new UserDto(10L, "nameDto", "emailDto@ru");

        User result = UserMapper.mapToNewUser(userDto);

        assertNull(result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void mapListToUserDto_whenInvoke_thenReturnListOfUserDto() {
        Collection<User> listOfUsers = List.of(
                new User(1L, "user1", "email1@ru"),
                new User(2L, "user2", "email2@ru"));

        Collection<UserDto> resultList = UserMapper.mapListToUserDto(listOfUsers);

        assertEquals(listOfUsers.size(), resultList.size());
    }

}