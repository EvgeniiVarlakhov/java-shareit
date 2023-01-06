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
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
class UserControllerIntegrTest {
    private UserDto userDtoIn;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService userService;

    @BeforeEach
    public void createUserDtoIn() {
        userDtoIn = new UserDto(1L, "name", "email@ru");
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenInvoke_thenStatusIsOk() {
        Collection<UserDto> userDtoCollection = List.of(userDtoIn);
        when(userService.getAllUsers()).thenReturn(userDtoCollection);

        String result = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).getAllUsers();
        assertEquals(objectMapper.writeValueAsString(userDtoCollection), result);
    }

    @SneakyThrows
    @Test
    void getUserById_whenInvoke_thenStatusIsOkAndReturnUserDto() {
        long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(userDtoIn);

        String result = mvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).getUserById(userId);
        assertEquals(objectMapper.writeValueAsString(userDtoIn), result);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserValidatedIsOk_thenReturnUser() {
        when(userService.createUser(userDtoIn)).thenReturn(userDtoIn);

        String result = mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoIn), result);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserNameIsNull_thenReturnBadRequest() {
        userDtoIn.setName(null);
        when(userService.createUser(userDtoIn)).thenReturn(userDtoIn);

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(userDtoIn);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserNameIsBlank_thenReturnBadRequest() {
        userDtoIn.setName("");
        when(userService.createUser(userDtoIn)).thenReturn(userDtoIn);

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(userDtoIn);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserEmailIsBad_thenReturnBadRequest() {
        userDtoIn.setEmail("email");
        when(userService.createUser(userDtoIn)).thenReturn(userDtoIn);

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(userDtoIn);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserEmailIsBlank_thenReturnBadRequest() {
        userDtoIn.setEmail("");
        when(userService.createUser(userDtoIn)).thenReturn(userDtoIn);

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(userDtoIn);
    }

    @SneakyThrows
    @Test
    void createUser_whenUserEmailIsNull_thenReturnBadRequest() {
        userDtoIn.setEmail(null);
        when(userService.createUser(userDtoIn)).thenReturn(userDtoIn);

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(userDtoIn);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserValidateIsOk_thenReturnStatusIsOkAndUpdatedUser() {
        long userId = 1L;
        UserDto updateUser = new UserDto(1L, "newName", "newEmail@ru");
        userDtoIn.setName("newName");
        userDtoIn.setEmail("newEmail@ru");
        when(userService.updateUser(userDtoIn, userId)).thenReturn(userDtoIn);

        String result = mvc.perform(patch("/users/{id}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).updateUser(userDtoIn, userId);
        assertEquals(objectMapper.writeValueAsString(updateUser), result);

    }

    @SneakyThrows
    @Test
    void updateUser_whenUserEmailIsBad_thenReturnBadRequest() {
        long userId = 1L;
        userDtoIn.setEmail("email");
        when(userService.updateUser(userDtoIn, userId)).thenReturn(userDtoIn);

        mvc.perform(patch("/users/{id}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoIn)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(userDtoIn, userId);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        long userId = 1L;

        mvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }

}