package com.bernardoms.campsitescheduleapi.validation;

import com.bernardoms.campsitescheduleapi.model.AvailabilityRequestParam;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MaxAvailabilityDayValidator implements ConstraintValidator<MaxAvailabilityDayValidation, AvailabilityRequestParam> {

    @Value("${maxAvailabilityDay}")
    protected Integer maxValue;

    @Override
    public boolean isValid(AvailabilityRequestParam availabilityRequestParam, ConstraintValidatorContext constraintValidatorContext) {

        if (Objects.isNull(maxValue)) {
            maxValue = 30;
        }

        if(!Objects.isNull(availabilityRequestParam.getStartDate()) && !Objects.isNull(availabilityRequestParam.getEndDate())) {
            return ChronoUnit.DAYS.between(availabilityRequestParam.getStartDate(), availabilityRequestParam.getEndDate()) <= maxValue;
        }

        return true;
    }
}
