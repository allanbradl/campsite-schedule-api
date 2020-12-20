package com.bernardoms.campsitescheduleapi.service;

import com.bernardoms.campsitescheduleapi.dto.BookingDTO;
import com.bernardoms.campsitescheduleapi.exception.BookingDateUnavailableException;
import com.bernardoms.campsitescheduleapi.exception.BookingNotFoundException;
import com.bernardoms.campsitescheduleapi.exception.IllegalBookingStateException;
import com.bernardoms.campsitescheduleapi.model.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    Booking createBooking(BookingDTO bookingDTO) throws BookingDateUnavailableException;
    void cancelBooking(Long bookingId) throws BookingNotFoundException;
    BookingDTO updateBooking(Long bookingId, BookingDTO bookingDTO) throws BookingNotFoundException, IllegalBookingStateException, BookingDateUnavailableException;
    BookingDTO findBookingById(Long bookingId) throws BookingNotFoundException;
    List<LocalDate> getAvailableDatesForDateRange(LocalDate startDate, LocalDate endDate);
}
