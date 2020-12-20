package com.bernardoms.campsitescheduleapi.validation;

import com.bernardoms.campsitescheduleapi.model.AvailabilityRequestParam;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class StartDateBeforeEndDateValidatorRequestParam implements ConstraintValidator<StartDateBeforeEndDateValidationRequestParamValidation, AvailabilityRequestParam> {
    @Override
    public boolean isValid(AvailabilityRequestParam availabilityRequestParam, ConstraintValidatorContext constraintValidatorContext) {
        if(!Objects.isNull(availabilityRequestParam.getStartDate()) && !Objects.isNull(availabilityRequestParam.getEndDate())) {
            return availabilityRequestParam.getStartDate().isBefore(availabilityRequestParam.getEndDate());
        }
        return true;
    }
}
