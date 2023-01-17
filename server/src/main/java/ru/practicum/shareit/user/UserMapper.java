package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static User mapUpdateFromUserDto(UserDto userDto, User user) {
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return user;
    }

    public static User mapToNewUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static Collection<UserDto> mapListToUserDto(Collection<User> users) {
        Collection<UserDto> listUsers = new ArrayList<>();
        for (User user : users) {
            listUsers.add(UserMapper.toUserDto(user));
        }
        return listUsers;
    }

}
