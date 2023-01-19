package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemGateController.class)
class ItemGateControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemClient itemClient;

    @SneakyThrows
    @Test
    void getAllItems_whenWithoutParams_thenStatusOkAndParamIsDefault() {
        long userId = 1L;

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient).getAllItems(userId, 0, 10);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenParamStartLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(-10))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllItems(userId, -10, 20);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenParamSizeLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-20)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllItems(userId, 0, -20);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenParamSizeIsZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllItems(userId, 0, 0);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenWithoutSizeParam_thenStatusOkAndSizeParamIsDefault() {
        long userId = 1L;

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2)))
                .andExpect(status().isOk());

        verify(itemClient).getAllItems(userId, 2, 10);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenWithParams_thenStatusOk() {
        long userId = 1L;

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isOk());

        verify(itemClient).getAllItems(userId, 2, 20);
    }

    @SneakyThrows
    @Test
    void searchItemByName_whenWithoutParams_thenStatusOkAndParamIsDefault() {
        long userId = 1L;

        String result = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemClient).searchItemByName(userId, " ", 0, 10);
    }

    @SneakyThrows
    @Test
    void searchItemByName_whenParamStartLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(-10))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).searchItemByName(userId, " ", -10, 20);
    }

    @SneakyThrows
    @Test
    void searchItemByName_whenParamSizeLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-20)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).searchItemByName(userId, " ", 0, -20);
    }

    @SneakyThrows
    @Test
    void searchItemByName_whenParamSizeIsZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).searchItemByName(userId, " ", 0, 0);
    }

    @SneakyThrows
    @Test
    void searchItemByName_whenWithoutSizeParam_thenStatusOkAndSizeParamIsDefault() {
        long userId = 1L;

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2)))
                .andExpect(status().isOk());

        verify(itemClient).searchItemByName(userId, " ", 2, 10);
    }

    @SneakyThrows
    @Test
    void searchItemByName_whenWithParams_thenStatusOk() {
        long userId = 1L;
        String search = "text";

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2))
                        .param("size", String.valueOf(20))
                        .param(search, search))
                .andExpect(status().isOk());

        verify(itemClient).searchItemByName(userId, search, 2, 20);
    }

    @SneakyThrows
    @Test
    void createItem_whenNameIsNull_thenReturnBadRequest() {
        long userId = 1L;
        ItemDto newItemDto = new ItemDto(1L, "name", "desc", true, 2L, 1L);
        newItemDto.setName(null);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, newItemDto);
    }

    @SneakyThrows
    @Test
    void createItem_whenNameIsBlank_thenReturnBadRequest() {
        long userId = 1L;
        ItemDto newItemDto = new ItemDto(1L, "name", "desc", true, 2L, 1L);
        newItemDto.setName(" ");

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, newItemDto);
    }

    @SneakyThrows
    @Test
    void createItem_whenDescriptionIsNull_thenReturnBadRequest() {
        long userId = 1L;
        ItemDto newItemDto = new ItemDto(1L, "name", "desc", true, 2L, 1L);
        newItemDto.setDescription(null);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, newItemDto);
    }

    @SneakyThrows
    @Test
    void createItem_whenDescriptionIsBlank_thenReturnBadRequest() {
        long userId = 1L;
        ItemDto newItemDto = new ItemDto(1L, "name", "desc", true, 2L, 1L);
        newItemDto.setDescription(" ");

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, newItemDto);
    }

    @SneakyThrows
    @Test
    void createItem_whenAvailableIsNull_thenReturnBadRequest() {
        long userId = 1L;
        ItemDto newItemDto = new ItemDto(1L, "name", "desc", true, 2L, 1L);
        newItemDto.setAvailable(null);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, newItemDto);
    }

    @SneakyThrows
    @Test
    void createComment_whenCommentIsNull_thenReturnBadRequest() {
        long userId = 1L;
        long itemId = 2L;
        CommentDtoIn newComment = new CommentDtoIn(null);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createComment(itemId, userId, newComment);
    }

    @SneakyThrows
    @Test
    void createComment_whenCommentIsBlank_thenReturnBadRequest() {
        long userId = 1L;
        long itemId = 2L;
        CommentDtoIn newComment = new CommentDtoIn(" ");

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createComment(itemId, userId, newComment);
    }

    @SneakyThrows
    @Test
    void deleteItem_whenInvoke_thenReturnStatusIsOk() {
        long userId = 1L;
        long itemId = 2L;

        mvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient).deleteItem(itemId, userId);
    }

}