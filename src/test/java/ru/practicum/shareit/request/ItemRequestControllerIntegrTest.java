package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOutWithReplies;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIntegrTest {
    private ItemRequestDtoOutWithReplies itemRequestWithReplies;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemRequestService service;


    @BeforeEach
    public void createRequest() {
        itemRequestWithReplies = new ItemRequestDtoOutWithReplies(
                1L, "desc1", LocalDateTime.now(), new ArrayList<>());
    }

    @SneakyThrows
    @Test
    void getListOfItemRequestByRequestor_whenInvoke_thenRerurnListOfRequest() {
        long userId = 1L;
        Collection<ItemRequestDtoOutWithReplies> listRequest = List.of(itemRequestWithReplies);
        when(service.getListOfItemRequestByRequestor(userId)).thenReturn(listRequest);

        String result = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getListOfItemRequestByRequestor(userId);
        assertEquals(objectMapper.writeValueAsString(listRequest), result);
    }

    @SneakyThrows
    @Test
    void getItemRequestById_whenInvoke_thenReturnRequest() {
        long userId = 1L;
        long requestId = 2L;
        when(service.getItemRequestById(userId, requestId)).thenReturn(itemRequestWithReplies);

        String result = mvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getItemRequestById(userId, requestId);
        assertEquals(objectMapper.writeValueAsString(itemRequestWithReplies), result);
    }

    @SneakyThrows
    @Test
    void getListOfItemRequestByAllUsers_whenWithoutParams_thenStatusOkAndParamIsDefault() {
        long userId = 1L;
        Collection<ItemRequestDtoOutWithReplies> listRequest = List.of(itemRequestWithReplies);
        when(service.getListOfItemRequestByAllUsers(userId, 0, 10)).thenReturn(listRequest);

        String result = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getListOfItemRequestByAllUsers(userId, 0, 10);
        assertEquals(objectMapper.writeValueAsString(listRequest), result);
    }

    @SneakyThrows
    @Test
    void getListOfItemRequestByAllUsers_whenWithoutSizeParam_thenStatusOkAndSizeParamIsDefault() {
        long userId = 1L;
        Collection<ItemRequestDtoOutWithReplies> listRequest = List.of(itemRequestWithReplies);
        when(service.getListOfItemRequestByAllUsers(userId, 2, 10)).thenReturn(listRequest);

        String result = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getListOfItemRequestByAllUsers(userId, 2, 10);
        assertEquals(objectMapper.writeValueAsString(listRequest), result);
    }

    @SneakyThrows
    @Test
    void getListOfItemRequestByAllUsers_whenWithParams_thenStatusOk() {
        long userId = 1L;
        Collection<ItemRequestDtoOutWithReplies> listRequest = List.of(itemRequestWithReplies);
        when(service.getListOfItemRequestByAllUsers(userId, 2, 20)).thenReturn(listRequest);

        String result = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(2))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getListOfItemRequestByAllUsers(userId, 2, 20);
        assertEquals(objectMapper.writeValueAsString(listRequest), result);
    }

    @SneakyThrows
    @Test
    void getListOfItemRequestByAllUsers_whenParamFromLessZero_then() {
        long userId = 1L;
        Collection<ItemRequestDtoOutWithReplies> listRequest = List.of(itemRequestWithReplies);
        when(service.getListOfItemRequestByAllUsers(userId, 2, 20)).thenReturn(listRequest);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(-10))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isBadRequest());

        verify(service, never()).getListOfItemRequestByAllUsers(userId, -10, 20);
    }

    @SneakyThrows
    @Test
    void getListOfItemRequestByAllUsers_whenParamSizeLessZero_then() {
        long userId = 1L;
        Collection<ItemRequestDtoOutWithReplies> listRequest = List.of(itemRequestWithReplies);
        when(service.getListOfItemRequestByAllUsers(userId, 2, 20)).thenReturn(listRequest);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-20)))
                .andExpect(status().isBadRequest());

        verify(service, never()).getListOfItemRequestByAllUsers(userId, 0, -20);
    }

    @SneakyThrows
    @Test
    void getListOfItemRequestByAllUsers_whenParamSizeIsZero_then() {
        long userId = 1L;
        Collection<ItemRequestDtoOutWithReplies> listRequest = List.of(itemRequestWithReplies);
        when(service.getListOfItemRequestByAllUsers(userId, 2, 20)).thenReturn(listRequest);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest());

        verify(service, never()).getListOfItemRequestByAllUsers(userId, 0, 0);
    }

    @SneakyThrows
    @Test
    void createItemRequest_whenRequestIsNull_thenReturnBadRequest() {
        long userId = 1L;
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn(null);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn)))
                .andExpect(status().isBadRequest());

        verify(service, never()).createItemRequest(itemRequestDtoIn, userId);
    }

    @SneakyThrows
    @Test
    void createItemRequest_whenRequestIsBlank_thenReturnBadRequest() {
        long userId = 1L;
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn("");

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn)))
                .andExpect(status().isBadRequest());

        verify(service, never()).createItemRequest(itemRequestDtoIn, userId);
    }

    @SneakyThrows
    @Test
    void createItemRequest_whenRequestIsValidate_thenReturnStatusOk() {
        long userId = 1L;
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn("desc");

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn)))
                .andExpect(status().isOk());

        verify(service).createItemRequest(itemRequestDtoIn, userId);
    }

}