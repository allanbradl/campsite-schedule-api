package com.bernardoms.campsitescheduleapi.model;

import com.bernardoms.campsitescheduleapi.validation.MaxAvailabilityDayValidation;
import com.bernardoms.campsitescheduleapi.validation.StartDateBeforeEndDateValidationRequestParamValidation;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@StartDateBeforeEndDateValidationRequestParamValidation
@MaxAvailabilityDayValidation
public class AvailabilityRequestParam {
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
