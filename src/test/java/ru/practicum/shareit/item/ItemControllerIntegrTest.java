package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIntegrTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemService itemService;

    @SneakyThrows
    @Test
    void getAllItems_whenWithoutParams_thenStatusOkAndParamIsDefault() {
        long userId = 1L;
        Collection<ItemDtoForOwner> listOfItem = List.of(new ItemDtoForOwner());
        when(itemService.getAllItems(userId, 0, 10)).thenReturn(listOfItem);

        String result = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getAllItems(userId, 0, 10);
        assertEquals(objectMapper.writeValueAsString(listOfItem), result);
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

        verify(itemService, never()).getAllItems(userId, -10, 20);
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

        verify(itemService, never()).getAllItems(userId, 0, -20);
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

        verify(itemService, never()).getAllItems(userId, 0, 0);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenWithoutSizeParam_thenStatusOkAndSizeParamIsDefault() {
        long userId = 1L;
        Collection<ItemDtoForOwner> listOfItem = List.of(new ItemDtoForOwner());
        when(itemService.getAllItems(userId, 2, 10)).thenReturn(listOfItem);

        String result = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getAllItems(userId, 2, 10);
        assertEquals(objectMapper.writeValueAsString(listOfItem), result);
    }

    @SneakyThrows
    @Test
    void getAllItems_whenWithParams_thenStatusOk() {
        long userId = 1L;
        Collection<ItemDtoForOwner> listOfItem = List.of(new ItemDtoForOwner());
        when(itemService.getAllItems(userId, 2, 20)).thenReturn(listOfItem);

        String result = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getAllItems(userId, 2, 20);
        assertEquals(objectMapper.writeValueAsString(listOfItem), result);
    }

    @SneakyThrows
    @Test
    void getItemById_whenInvoke_thenReturnItemDto() {
        long userId = 1L;
        long itemId = 2L;
        ItemDtoForOwner newItem = new ItemDtoForOwner();
        when(itemService.getItemById(userId, itemId)).thenReturn(newItem);

        String result = mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(newItem), result);
    }

    @SneakyThrows
    @Test
    void searchItemByName_whenWithoutParams_thenStatusOkAndParamIsDefault() {
        long userId = 1L;
        Collection<ItemDtoForBooker> listOfItem = List.of(new ItemDtoForBooker());
        when(itemService.searchItemByName(userId, " ", 0, 10)).thenReturn(listOfItem);

        String result = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).searchItemByName(userId, " ", 0, 10);
        assertEquals(objectMapper.writeValueAsString(listOfItem), result);
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

        verify(itemService, never()).searchItemByName(userId, " ", -10, 20);
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

        verify(itemService, never()).searchItemByName(userId, " ", 0, -20);
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

        verify(itemService, never()).searchItemByName(userId, " ", 0, 0);
    }

    @SneakyThrows
    @Test
    void searchItemByName_whenWithoutSizeParam_thenStatusOkAndSizeParamIsDefault() {
        long userId = 1L;
        Collection<ItemDtoForBooker> listOfItem = List.of(new ItemDtoForBooker());
        when(itemService.searchItemByName(userId, " ", 2, 10)).thenReturn(listOfItem);

        String result = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).searchItemByName(userId, " ", 2, 10);
        assertEquals(objectMapper.writeValueAsString(listOfItem), result);
    }

    @SneakyThrows
    @Test
    void searchItemByName_whenWithParams_thenStatusOk() {
        long userId = 1L;
        String search = "text";
        Collection<ItemDtoForBooker> listOfItem = List.of(new ItemDtoForBooker());
        when(itemService.searchItemByName(userId, search, 2, 20)).thenReturn(listOfItem);

        String result = mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2))
                        .param("size", String.valueOf(20))
                        .param(search, search))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).searchItemByName(userId, search, 2, 20);
        assertEquals(objectMapper.writeValueAsString(listOfItem), result);
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

        verify(itemService, never()).createItem(userId, newItemDto);
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

        verify(itemService, never()).createItem(userId, newItemDto);
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

        verify(itemService, never()).createItem(userId, newItemDto);
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

        verify(itemService, never()).createItem(userId, newItemDto);
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

        verify(itemService, never()).createItem(userId, newItemDto);
    }

    @SneakyThrows
    @Test
    void createItem_whenValidateIsOk_thenReturnStatusOk() {
        long userId = 1L;
        ItemDto newItemDto = new ItemDto(1L, "name", "desc", true, 2L, 1L);
        when(itemService.createItem(userId, newItemDto)).thenReturn(newItemDto);

        String result = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(newItemDto), result);
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

        verify(itemService, never()).createComment(itemId, userId, newComment);
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

        verify(itemService, never()).createComment(itemId, userId, newComment);
    }

    @SneakyThrows
    @Test
    void createComment_whenCommentIsValidate_thenReturnStatusIsOk() {
        long userId = 1L;
        long itemId = 2L;
        CommentDtoIn newComment = new CommentDtoIn("text");
        CommentDtoOut comment = new CommentDtoOut(
                1L,
                "text",
                "name",
                LocalDateTime.now()
        );
        when(itemService.createComment(itemId, userId, newComment)).thenReturn(comment);

        String result = mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(comment), result);
    }

    @SneakyThrows
    @Test
    void updateItem_whenInvoke_thenReturnStatusIsOk() {
        long userId = 1L;
        long itemId = 2L;
        ItemDto newItemDto = new ItemDto(1L, "name", "desc", true, 2L, 1L);
        when(itemService.updateItem(itemId, userId, newItemDto)).thenReturn(newItemDto);

        String result = mvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(newItemDto), result);
    }

    @SneakyThrows
    @Test
    void deleteItem_whenInvoke_thenReturnStatusIsOk() {
        long userId = 1L;
        long itemId = 2L;

        mvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(itemId, userId);
    }

}