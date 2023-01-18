package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserGateController.class)
class UserGateControllerTest {
    private UserDto userDtoIn;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserClient userClient;

    @BeforeEach
    public void createUserDtoIn() {
        userDtoIn = new UserDto(1L, "name", "email@ru");
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenInvoke_thenStatusIsOk() {
        mvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).getAllUsers();
    }

    @SneakyThrows
    @Test
    void getUserById_whenInvoke_thenStatusIsOkAndReturnUserDto() {
        long userId = 1L;

        mvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userClient).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserValidatedIsOk_thenReturnStatusIsOk() {

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isOk());

        verify(userClient).createUser(userDtoIn);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserNameIsNull_thenReturnBadRequest() {
        userDtoIn.setName(null);

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(userDtoIn);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserNameIsBlank_thenReturnBadRequest() {
        userDtoIn.setName("");

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(userDtoIn);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserEmailIsBad_thenReturnBadRequest() {
        userDtoIn.setEmail("email");

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(userDtoIn);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserEmailIsBlank_thenReturnBadRequest() {
        userDtoIn.setEmail("");

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(userDtoIn);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserEmailIsNull_thenReturnBadRequest() {
        userDtoIn.setEmail(null);

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(userDtoIn);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserValidateIsOk_thenReturnStatusIsOk() {
        long userId = 1L;
        userDtoIn.setName("newName");
        userDtoIn.setEmail("newEmail@ru");

        mvc.perform(patch("/users/{id}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isOk());

        verify(userClient).updateUser(userDtoIn, userId);

    }

    @SneakyThrows
    @Test
    void updateUser_whenUserEmailIsBad_thenReturnBadRequest() {
        long userId = 1L;
        userDtoIn.setEmail("email");

        mvc.perform(patch("/users/{id}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(userDtoIn, userId);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        long userId = 1L;

        mvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(userId);
    }

}