package com.bernardoms.campsitescheduleapi.validation;

import com.bernardoms.campsitescheduleapi.model.AvailabilityRequestParam;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartDateBeforeEndDateValidatorRequestParam implements ConstraintValidator<StartDateBeforeEndDateValidationRequestParamValidation, AvailabilityRequestParam> {
    @Override
    public boolean isValid(AvailabilityRequestParam availabilityRequestParam, ConstraintValidatorContext constraintValidatorContext) {
        return availabilityRequestParam.getStartDate().isBefore(availabilityRequestParam.getEndDate());
    }
}
