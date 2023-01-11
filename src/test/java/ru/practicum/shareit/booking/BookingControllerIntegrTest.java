package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoFullOut;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIntegrTest {
    private BookingDtoFullOut bookingDtoFullOut;
    private BookingDtoIn bookingDtoIn;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    BookingService bookingService;

    @BeforeEach
    void createBookings() {
        bookingDtoFullOut = new BookingDtoFullOut(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                "status",
                new BookingDtoFullOut.UserInfo(2L),
                new BookingDtoFullOut.ItemInfo(1L, "name")
        );

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
        Collection<BookingDtoFullOut> listOfBooking = List.of(bookingDtoFullOut);
        when(bookingService.getListOfBookingsBooker(userId, "ALL", 0, 10))
                .thenReturn(listOfBooking);

        String result = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getListOfBookingsBooker(userId, "ALL", 0, 10);
        assertEquals(objectMapper.writeValueAsString(listOfBooking), result);
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

        verify(bookingService, never()).getListOfBookingsBooker(userId, "ALL", -10, 20);
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

        verify(bookingService, never()).getListOfBookingsBooker(userId, "ALL", 0, -20);
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

        verify(bookingService, never()).getListOfBookingsBooker(userId, "ALL", 0, 0);
    }

    @SneakyThrows
    @Test
    void getListOfBookingsBooker_whenWithParams_thenStatusOkAndReturnCollection() {
        long userId = 1L;
        Collection<BookingDtoFullOut> listOfBooking = List.of(bookingDtoFullOut);
        when(bookingService.getListOfBookingsBooker(userId, "STATE", 1, 20))
                .thenReturn(listOfBooking);

        String result = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "STATE")
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getListOfBookingsBooker(userId, "STATE", 1, 20);
        assertEquals(objectMapper.writeValueAsString(listOfBooking), result);
    }

    @SneakyThrows
    @Test
    void getListOfBookingsOwner_whenWithoutParams_thenStatusOkAndParamIsDefault() {
        long userId = 1L;
        Collection<BookingDtoFullOut> listOfBooking = List.of(bookingDtoFullOut);
        when(bookingService.getListOfBookingsOwner(userId, "ALL", 0, 10)).thenReturn(listOfBooking);

        String result = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getListOfBookingsOwner(userId, "ALL", 0, 10);
        assertEquals(objectMapper.writeValueAsString(listOfBooking), result);
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

        verify(bookingService, never()).getListOfBookingsOwner(userId, "ALL", -10, 20);
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

        verify(bookingService, never()).getListOfBookingsOwner(userId, "ALL", 0, -20);
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

        verify(bookingService, never()).getListOfBookingsOwner(userId, "ALL", 0, 0);
    }

    @SneakyThrows
    @Test
    void getListOfBookingsOwner_whenWithParams_thenStatusOkAndReturnCollection() {
        long userId = 1L;
        Collection<BookingDtoFullOut> listOfBooking = List.of(bookingDtoFullOut);
        when(bookingService.getListOfBookingsOwner(userId, "STATE", 1, 20))
                .thenReturn(listOfBooking);

        String result = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "STATE")
                        .param("from", String.valueOf(1))
                        .param("size", String.valueOf(20)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getListOfBookingsOwner(userId, "STATE", 1, 20);
        assertEquals(objectMapper.writeValueAsString(listOfBooking), result);
    }

    @SneakyThrows
    @Test
    void getBookingInfo_whenInvoke_thenReturnBookingDtoFullOut() {
        long userId = 1L;
        long bookingId = 2L;
        when(bookingService.getBookingInfo(userId, bookingId)).thenReturn(bookingDtoFullOut);

        String result = mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoFullOut), result);
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

        verify(bookingService, never()).createBooking(bookingDtoIn, userId);
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

        verify(bookingService, never()).createBooking(bookingDtoIn, userId);
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

        verify(bookingService, never()).createBooking(bookingDtoIn, userId);
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

        verify(bookingService, never()).createBooking(bookingDtoIn, userId);
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

        verify(bookingService, never()).createBooking(bookingDtoIn, userId);
    }

    @SneakyThrows
    @Test
    void createBooking_whenValidateIsOk_thenReturnStatusIsOk() {
        long userId = 1L;
        when(bookingService.createBooking(bookingDtoIn, userId)).thenReturn(bookingDtoFullOut);

        String result = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoFullOut), result);
    }

    @SneakyThrows
    @Test
    void doApprovedBooking_whenWithoutParam_thenReturnStatusIs500() {
        long userId = 1L;
        long bookingId = 2L;
        String approved = "app";
        when(bookingService.getApprovedBooking(bookingId, userId, approved)).thenReturn(bookingDtoFullOut);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().is5xxServerError());

        verify(bookingService, never()).getApprovedBooking(bookingId, userId, approved);
    }

    @SneakyThrows
    @Test
    void doApprovedBooking_whenWithParam_thenReturnStatusIsOk() {
        long userId = 1L;
        long bookingId = 2L;
        String approved = "app";
        when(bookingService.getApprovedBooking(bookingId, userId, approved)).thenReturn(bookingDtoFullOut);

        String result = mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", approved))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoFullOut), result);
    }

}