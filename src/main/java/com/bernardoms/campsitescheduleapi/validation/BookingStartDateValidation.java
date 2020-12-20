package com.bernardoms.campsitescheduleapi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BookingStartDateValidator.class})
@Documented
public @interface BookingStartDateValidation {
    String message() default "Booking start date should be minimum of 1 day ahead and maximum of 30 days advance";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
