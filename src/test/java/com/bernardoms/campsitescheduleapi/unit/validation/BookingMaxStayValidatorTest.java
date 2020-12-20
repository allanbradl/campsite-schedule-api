package com.bernardoms.campsitescheduleapi.unit.validation;

import com.bernardoms.campsitescheduleapi.dto.BookingDTO;
import com.bernardoms.campsitescheduleapi.validation.BookingMaxStayValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingMaxStayValidatorTest {

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Test
    void shouldValidateBookingMaxStay() {
        var bookingMaxStayValidator = new BookingMaxStayValidator();

        var notValidBookingDTO = BookingDTO.builder()
                .fullName("test test")
                .email("test@test.com")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(4)).build();

        var validBookingDTO = BookingDTO.builder()
                .fullName("test test")
                .email("test@test.com")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(3)).build();

        assertFalse(bookingMaxStayValidator.isValid(notValidBookingDTO, constraintValidatorContext));
        assertTrue(bookingMaxStayValidator.isValid(validBookingDTO, constraintValidatorContext));
    }
}
