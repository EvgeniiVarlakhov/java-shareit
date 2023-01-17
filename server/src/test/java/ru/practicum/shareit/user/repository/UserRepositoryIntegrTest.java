package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryIntegrTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void addUser() {
        userRepository.save(new User(1L, "name", "email@ru"));
    }

    @DirtiesContext
    @Test
    void findByEmail_whenUserFind_thenUserReturn() {
        Optional<User> result = userRepository.findByEmail("email@ru");

        assertTrue(result.isPresent());
        assertEquals("name", result.get().getName());
    }

    @DirtiesContext
    @Test
    void findByEmail_whenUserNotFind_thenReturnEmptyOptional() {
        Optional<User> result = userRepository.findByEmail("e@ru");

        assertTrue(result.isEmpty());
    }

}