package com.bernardoms.campsitescheduleapi.unit.validation;

import com.bernardoms.campsitescheduleapi.dto.BookingDTO;
import com.bernardoms.campsitescheduleapi.model.AvailabilityRequestParam;
import com.bernardoms.campsitescheduleapi.validation.StartDateBeforeEndDateValidator;
import com.bernardoms.campsitescheduleapi.validation.StartDateBeforeEndDateValidatorRequestParam;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StartDateBeforeEndDateValidatorRequestParamTest {

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Test
    void shouldValidateBookingStartDateIsBeforeEndDateForRequestParam() {
        var startDateBeforeEndDateValidator = new StartDateBeforeEndDateValidatorRequestParam();

        var invalidRequest = AvailabilityRequestParam.builder()
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now()).build();


        var validRequest = AvailabilityRequestParam.builder()
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(3)).build();

        assertFalse(startDateBeforeEndDateValidator.isValid(invalidRequest, constraintValidatorContext));

        assertTrue(startDateBeforeEndDateValidator.isValid(validRequest, constraintValidatorContext));
    }
}
