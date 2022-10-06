package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    User addUser(User user);

    void deleteUser(long userId);

    User getUserById(long userId);

    Collection<User> getUsersList();

}
