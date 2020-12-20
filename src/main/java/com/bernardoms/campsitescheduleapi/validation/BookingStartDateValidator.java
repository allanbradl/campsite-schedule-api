package com.bernardoms.campsitescheduleapi.validation;

import com.bernardoms.campsitescheduleapi.dto.BookingDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BookingStartDateValidator implements ConstraintValidator<BookingStartDateValidation, BookingDTO> {
    @Override
    public boolean isValid(BookingDTO bookingDTO, ConstraintValidatorContext constraintValidatorContext) {
        return LocalDate.now().isBefore(bookingDTO.getStartDate())
                && bookingDTO.getStartDate().isBefore(LocalDate.now().plusMonths(1).plusDays(1));
    }
}
