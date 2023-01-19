package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingGateController.class)
class BookingGateControllerTest {
    private BookingDtoIn bookingDtoIn;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    BookingClient bookingClient;

    @BeforeEach
    void createBookings() {

        bookingDtoIn = new BookingDtoIn(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(10),
                1L,
                1L,
                "STATUS"
        );
    }

    @SneakyThrows
    @Test
    void getListOfBookingsBooker_whenWithoutParams_thenStatusOkAndParamIsDefault() {
        long userId = 1L;

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient).getListOfBookingsBooker(userId, BookingState.ALL, 0, 10);
    }

    @SneakyThrows
    @Test
    void getListOfBookingsBooker_whenParamStartLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(-10))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getListOfBookingsBooker(userId, BookingState.ALL, -10, 20);
    }

    @SneakyThrows
    @Test
    void getListOfBookingsBooker_whenParamSizeLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-20)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getListOfBookingsBooker(userId, BookingState.ALL, 0, -20);
    }

    @SneakyThrows
    @Test
    void getListOfBookingsBooker_whenParamSizeIsZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getListOfBookingsBooker(userId, BookingState.ALL, 0, 0);
    }

    @SneakyThrows
    @Test
    void getListOfBookingsBooker_whenWithParams_thenStatusOk() {
        long userId = 1L;

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isOk());

        verify(bookingClient).getListOfBookingsBooker(userId, BookingState.ALL, 1, 20);
    }

    @SneakyThrows
    @Test
    void getListOfBookingsOwner_whenWithoutParams_thenStatusOkAndParamIsDefault() {
        long userId = 1L;

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient).getListOfBookingsOwner(userId, BookingState.ALL, 0, 10);
    }

    @SneakyThrows
    @Test
    void getListOfBookingsOwner_whenParamStartLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(-10))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getListOfBookingsOwner(userId, BookingState.ALL, -10, 20);
    }

    @SneakyThrows
    @Test
    void getListOfBookingsOwner_whenParamSizeLessZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(-20)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getListOfBookingsOwner(userId, BookingState.ALL, 0, -20);
    }

    @SneakyThrows
    @Test
    void getListOfBookingsOwner_whenParamSizeIsZero_thenStatusBadRequest() {
        long userId = 1L;

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getListOfBookingsOwner(userId, BookingState.ALL, 0, 0);
    }

    @SneakyThrows
    @Test
    void getListOfBookingsOwner_whenWithParams_thenStatusOk() {
        long userId = 1L;

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isOk());

        verify(bookingClient).getListOfBookingsOwner(userId, BookingState.ALL, 1, 20);
    }

    @SneakyThrows
    @Test
    void createBooking_whenStartDateIsNull_thenReturnBadRequest() {
        long userId = 1L;
        bookingDtoIn.setStart(null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(userId, bookingDtoIn);
    }

    @SneakyThrows
    @Test
    void createBooking_whenStartDateInPast_thenReturnBadRequest() {
        long userId = 1L;
        bookingDtoIn.setStart(LocalDateTime.now().minusDays(10));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(userId, bookingDtoIn);
    }

    @SneakyThrows
    @Test
    void createBooking_whenEndDateIsNull_thenReturnBadRequest() {
        long userId = 1L;
        bookingDtoIn.setEnd(null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(userId, bookingDtoIn);
    }

    @SneakyThrows
    @Test
    void createBooking_whenEndDateInPast_thenReturnBadRequest() {
        long userId = 1L;
        bookingDtoIn.setEnd(LocalDateTime.now().minusDays(10));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(userId, bookingDtoIn);
    }

    @SneakyThrows
    @Test
    void createBooking_whenItemIdIsNull_thenReturnBadRequest() {
        long userId = 1L;
        bookingDtoIn.setItemId(null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(userId, bookingDtoIn);
    }

    @SneakyThrows
    @Test
    void doApprovedBooking_whenWithoutParam_thenReturnStatusIs500() {
        long userId = 1L;
        long bookingId = 2L;
        String approved = "app";

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is5xxServerError());

        verify(bookingClient, never()).doApprovedBooking(bookingId, userId, approved);
    }

}