package com.bernardoms.campsitescheduleapi.unit.validation;

import com.bernardoms.campsitescheduleapi.model.AvailabilityRequestParam;
import com.bernardoms.campsitescheduleapi.validation.MaxAvailabilityDayValidator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MaxAvailabilityDayValidatorTest {

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Test
    void shouldValidateBookingMaxStay() {
        var  maxAvailabilityDayValidator = new MaxAvailabilityDayValidator();

        var invalidAvailability = AvailabilityRequestParam.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1).plusDays(2)).build();

        var validAvailability = AvailabilityRequestParam.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1)).build();

        assertFalse(maxAvailabilityDayValidator.isValid(invalidAvailability, constraintValidatorContext));
        assertTrue(maxAvailabilityDayValidator.isValid(validAvailability, constraintValidatorContext));
    }
}
