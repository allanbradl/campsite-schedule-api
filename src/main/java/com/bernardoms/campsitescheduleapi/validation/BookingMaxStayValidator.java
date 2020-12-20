package com.bernardoms.campsitescheduleapi.validation;

import com.bernardoms.campsitescheduleapi.dto.BookingDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.temporal.ChronoUnit;

public class BookingMaxStayValidator implements ConstraintValidator<BookingMaxStayValidation, BookingDTO> {
    @Override
    public boolean isValid(BookingDTO bookingDTO, ConstraintValidatorContext constraintValidatorContext) {
        return ChronoUnit.DAYS.between(bookingDTO.getStartDate(), bookingDTO.getEndDate()) <= 3;
    }
}
