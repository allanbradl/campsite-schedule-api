package com.bernardoms.campsitescheduleapi.controller;

import com.bernardoms.campsitescheduleapi.dto.BookingDTO;
import com.bernardoms.campsitescheduleapi.exception.BookingDateUnavailableException;
import com.bernardoms.campsitescheduleapi.exception.BookingNotFoundException;
import com.bernardoms.campsitescheduleapi.exception.IllegalBookingStateException;
import com.bernardoms.campsitescheduleapi.model.AvailabilityRequestParam;
import com.bernardoms.campsitescheduleapi.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Long> createBooking(@RequestBody @Validated BookingDTO bookingDTO, UriComponentsBuilder uriComponentsBuilder) throws BookingDateUnavailableException {
        var uriComponent = uriComponentsBuilder.path("/v1/bookings/{bookingId}").buildAndExpand(bookingService.createBooking(bookingDTO).getId());
        return ResponseEntity.created(uriComponent.toUri()).build();
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBooking(@PathVariable Long bookingId) throws BookingNotFoundException {
        bookingService.cancelBooking(bookingId);
    }

    @PutMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDTO updateBooking(@PathVariable Long bookingId, @RequestBody @Validated BookingDTO bookingDTO) throws BookingNotFoundException, IllegalBookingStateException, BookingDateUnavailableException {
        return bookingService.updateBooking(bookingId, bookingDTO);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDTO getBooking(@PathVariable Long bookingId) throws BookingNotFoundException {
        return bookingService.findBookingById(bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LocalDate> getAvailableDate(
             @Validated AvailabilityRequestParam availabilityRequestParam) {
        return bookingService.getAvailableDatesForDateRange(availabilityRequestParam.getStartDate(), availabilityRequestParam.getEndDate());
    }
}
