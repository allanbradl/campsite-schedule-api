package com.bernardoms.campsitescheduleapi.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {StartDateBeforeEndDateValidatorRequestParam.class})
@Documented
public @interface StartDateBeforeEndDateValidationRequestParamValidation {
    String message() default "Start date should be before end date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
