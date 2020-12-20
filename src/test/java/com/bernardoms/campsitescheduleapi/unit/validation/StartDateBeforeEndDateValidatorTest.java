package com.bernardoms.campsitescheduleapi.unit.validation;

import com.bernardoms.campsitescheduleapi.dto.BookingDTO;
import com.bernardoms.campsitescheduleapi.validation.BookingStartDateValidator;
import com.bernardoms.campsitescheduleapi.validation.StartDateBeforeEndDateValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StartDateBeforeEndDateValidatorTest {

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Test
    void shouldValidateBookingStartDateIsBeforeEndDate() {
        var startDateBeforeEndDateValidator = new StartDateBeforeEndDateValidator();

        var notValidBookingDTO = BookingDTO.builder()
                .fullName("test test")
                .email("test@test.com")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now()).build();


        var validBookingDTO = BookingDTO.builder()
                .fullName("test test")
                .email("test@test.com")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(3)).build();

        assertFalse(startDateBeforeEndDateValidator.isValid(notValidBookingDTO, constraintValidatorContext));

        assertTrue(startDateBeforeEndDateValidator.isValid(validBookingDTO, constraintValidatorContext));
    }
}
