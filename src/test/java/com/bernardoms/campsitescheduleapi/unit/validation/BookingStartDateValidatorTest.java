package com.bernardoms.campsitescheduleapi.unit.validation;

import com.bernardoms.campsitescheduleapi.dto.BookingDTO;
import com.bernardoms.campsitescheduleapi.validation.BookingMaxStayValidator;
import com.bernardoms.campsitescheduleapi.validation.BookingStartDateValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingStartDateValidatorTest {

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Test
    void shouldValidateBookingStartDate() {
        var bookingStartDateValidator = new BookingStartDateValidator();

        var notValidBookingDTO = BookingDTO.builder()
                .fullName("test test")
                .email("test@test.com")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(4)).build();

        var notValidBookingDTO2 = BookingDTO.builder()
                .fullName("test test")
                .email("test@test.com")
                .startDate(LocalDate.now().plusDays(32))
                .endDate(LocalDate.now().plusDays(33)).build();

        var validBookingDTO = BookingDTO.builder()
                .fullName("test test")
                .email("test@test.com")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(3)).build();

        assertFalse(bookingStartDateValidator.isValid(notValidBookingDTO, constraintValidatorContext));

        assertFalse(bookingStartDateValidator.isValid(notValidBookingDTO2, constraintValidatorContext));

        assertTrue(bookingStartDateValidator.isValid(validBookingDTO, constraintValidatorContext));
    }
}
