package com.bernardoms.campsitescheduleapi.unit.controller;


import com.bernardoms.campsitescheduleapi.controller.BookingController;
import com.bernardoms.campsitescheduleapi.controller.ExceptionController;
import com.bernardoms.campsitescheduleapi.dto.BookingDTO;
import com.bernardoms.campsitescheduleapi.exception.BookingDateUnavailableException;
import com.bernardoms.campsitescheduleapi.exception.BookingNotFoundException;
import com.bernardoms.campsitescheduleapi.model.Booking;
import com.bernardoms.campsitescheduleapi.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String URL_PATH = "/v1/bookings";

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(bookingController)
                .setControllerAdvice(ExceptionController.class)
                .build();
    }

    @Test
    void shouldReturnOKWhenFindBookingById() throws Exception {
        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .fullName("test test")
                .build();

        when(bookingService.findBookingById(1L)).thenReturn(bookingDTO);

        mockMvc.perform(get(URL_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("fullName", is("test test")))
                .andExpect(jsonPath("email", is("test@test.com")));
    }

    @Test
    void shouldReturnNotFoundWhenFindBookingById() throws Exception {
        when(bookingService.findBookingById(1L)).thenThrow(new BookingNotFoundException("book not found!"));

        mockMvc.perform(get(URL_PATH + "/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("description", is("book not found!")));
    }

    @Test
    void shouldReturnCreatedWithBookingIdWhenCreatingABookWithSuccess() throws Exception {
        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .fullName("test test")
                .build();

        when(bookingService.createBooking(bookingDTO)).thenReturn(Booking.builder().id(1L).build());

        mockMvc.perform(post(URL_PATH).content(mapper.writeValueAsString(bookingDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("location", "http://localhost/v1/bookings/1"));
    }

    @Test
    void shouldReturnConflictedWhenCreatABookingWithUnavailableDate() throws Exception {
        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .fullName("test test")
                .build();

        when(bookingService.createBooking(bookingDTO)).thenThrow(new BookingDateUnavailableException("not available"));

        mockMvc.perform(post(URL_PATH).content(mapper.writeValueAsString(bookingDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldCancelBookingWithSuccess() throws Exception {
        mockMvc.perform(delete(URL_PATH + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenCancelANotFoundBooking() throws Exception {
        doThrow(new BookingNotFoundException("book not found!")).when(bookingService).cancelBooking(1L);

        mockMvc.perform(delete(URL_PATH + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAllAvailableDates() throws Exception {
        when(bookingService.getAvailableDatesForDateRange(LocalDate.now().plusDays(1),
                LocalDate.now().plusMonths(1))).thenReturn(LocalDate.now().plusDays(1)
                .datesUntil(LocalDate.now().plusMonths(1)).collect(Collectors.toList()));

        mockMvc.perform(get(URL_PATH)
                .param("startDate", LocalDate.now().plusDays(1).toString())
                .param("endDate", LocalDate.now().plusMonths(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",  hasSize(30)));
    }

    @Test
    void shouldReturnBadRequestWhenThereIsAValidationException() throws Exception {
        mockMvc.perform(get(URL_PATH)
                .param("startDate", LocalDate.now().plusMonths(30).toString())
                .param("endDate", LocalDate.now().plusMonths(1).toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOKWhenUpdatingABooking() throws Exception {
        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .fullName("test test")
                .build();

        mockMvc.perform(put(URL_PATH + "/1").content(mapper.writeValueAsString(bookingDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
