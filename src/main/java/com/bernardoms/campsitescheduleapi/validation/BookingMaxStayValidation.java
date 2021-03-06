package com.bernardoms.campsitescheduleapi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BookingMaxStayValidator.class})
@Documented
public @interface BookingMaxStayValidation {
    String message() default "Booking can be reserved just 3 days";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
