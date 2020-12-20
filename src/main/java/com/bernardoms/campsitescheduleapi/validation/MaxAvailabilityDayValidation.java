package com.bernardoms.campsitescheduleapi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {MaxAvailabilityDayValidator.class})
@Documented
public @interface MaxAvailabilityDayValidation {
    String message() default "Booking date range is higher than permitted";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
