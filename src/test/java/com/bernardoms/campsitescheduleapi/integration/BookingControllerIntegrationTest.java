package com.bernardoms.campsitescheduleapi.integration;

import com.bernardoms.campsitescheduleapi.dto.BookingDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String URL_PATH = "/v1/bookings";

    @Test
    void shouldReturnOKWhenFindBookingById() throws Exception {
        mockMvc.perform(get(URL_PATH + "/6542"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("fullName", is("test test")))
                .andExpect(jsonPath("email", is("abc@gmail.com")));
    }

    @Test
    void shouldReturnNotFoundWhenFindBookingById() throws Exception {
        mockMvc.perform(get(URL_PATH + "/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("description", is("Booking with id 1 not found!")));
    }

    @Test
    void shouldReturnCreatedWithBookingIdWhenCreatingABookWithSuccess() throws Exception {
        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .fullName("test test")
                .build();

        mockMvc.perform(post(URL_PATH).content(mapper.writeValueAsString(bookingDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("location", "http://localhost/v1/bookings/6544"));

        mockMvc.perform(delete(URL_PATH + "/6544"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnConflictedWhenCreateABookingWithUnavailableDate() throws Exception {
        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(3))
                .startDate(LocalDate.now().plusDays(1))
                .email("test@test.com")
                .fullName("test test")
                .build();
        mockMvc.perform(post(URL_PATH).content(mapper.writeValueAsString(bookingDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldCancelBookingWithSuccess() throws Exception {
        mockMvc.perform(delete(URL_PATH + "/6543"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenCancelANotFoundBooking() throws Exception {
        mockMvc.perform(delete(URL_PATH + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAllAvailableDates() throws Exception {
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

        mockMvc.perform(put(URL_PATH + "/6543").content(mapper.writeValueAsString(bookingDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
