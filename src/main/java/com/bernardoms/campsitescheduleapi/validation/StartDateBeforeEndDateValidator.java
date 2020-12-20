package com.bernardoms.campsitescheduleapi.validation;

import com.bernardoms.campsitescheduleapi.dto.BookingDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartDateBeforeEndDateValidator implements ConstraintValidator<StartDateBeforeEndDateValidation, BookingDTO> {
    @Override
    public boolean isValid(BookingDTO bookingDTO, ConstraintValidatorContext constraintValidatorContext) {
        return bookingDTO.getStartDate().isBefore(bookingDTO.getEndDate());
    }
}
