package com.bernardoms.campsitescheduleapi.dto;

import com.bernardoms.campsitescheduleapi.validation.BookingMaxStayValidation;
import com.bernardoms.campsitescheduleapi.validation.BookingStartDateValidation;
import com.bernardoms.campsitescheduleapi.validation.StartDateBeforeEndDateValidation;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@BookingMaxStayValidation
@BookingStartDateValidation
@StartDateBeforeEndDateValidation
public class BookingDTO {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String fullName;

    @NonNull
    @Future
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDate;
    @NonNull
    @Future
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate endDate;
}
