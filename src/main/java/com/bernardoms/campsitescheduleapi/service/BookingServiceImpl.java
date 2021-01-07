package com.bernardoms.campsitescheduleapi.service;

import com.bernardoms.campsitescheduleapi.dto.BookingDTO;
import com.bernardoms.campsitescheduleapi.exception.BookingDateUnavailableException;
import com.bernardoms.campsitescheduleapi.exception.BookingNotFoundException;
import com.bernardoms.campsitescheduleapi.exception.IllegalBookingStateException;
import com.bernardoms.campsitescheduleapi.model.Booking;
import com.bernardoms.campsitescheduleapi.model.BookingStatus;
import com.bernardoms.campsitescheduleapi.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(isolation= Isolation.SERIALIZABLE)
    @Retryable(include = JpaSystemException.class, maxAttempts = 2, backoff=@Backoff(delay = 150, maxDelay = 300))
    public Booking createBooking(BookingDTO bookingDTO) throws BookingDateUnavailableException {
        //TODO Check if already exist booking for giving email, if yes throws BookingAlreadyExists exception
        var availableDates = getAvailableDatesForDateRange(bookingDTO.getStartDate(), bookingDTO.getEndDate());

        var booking = modelMapper.map(bookingDTO, Booking.class);
        booking.setStatus(BookingStatus.ACTIVE);

        if (!availableDates.containsAll(booking.getAllDatesFromBookingDate())) {
            throw new BookingDateUnavailableException("no availability for " + booking.getStartDate() + " to " + booking.getEndDate());
        }

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) throws BookingNotFoundException {
        var bookingDTO = findBookingById(bookingId);

        var booking = modelMapper.map(bookingDTO, Booking.class);

        booking.setId(bookingId);
        booking.setStatus(BookingStatus.CANCELED);

        bookingRepository.save(booking);
    }

    @Override
    @Transactional(isolation= Isolation.SERIALIZABLE)
    @Retryable(include = JpaSystemException.class, maxAttempts = 2, backoff=@Backoff(delay = 300, maxDelay = 500))
    public BookingDTO updateBooking(Long bookingId, BookingDTO bookingDTO) throws BookingNotFoundException, IllegalBookingStateException, BookingDateUnavailableException {
        var foundBooking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found!"));

        if (BookingStatus.CANCELED.equals(foundBooking.getStatus())) {
            throw new IllegalBookingStateException("cant update a canceled booking");
        }

        var availableDatesForDateRange = getAvailableDatesForDateRange(bookingDTO.getStartDate(), bookingDTO.getEndDate());

        Booking booking = modelMapper.map(bookingDTO, Booking.class);

        if(!availableDatesForDateRange.containsAll(booking.getAllDatesFromBookingDate())) {
            throw new BookingDateUnavailableException("no availability for " + foundBooking.getStartDate() + " to " + foundBooking.getEndDate());
        }

        foundBooking.setEmail(bookingDTO.getEmail());
        foundBooking.setEndDate(bookingDTO.getEndDate());
        foundBooking.setStartDate(bookingDTO.getStartDate());
        foundBooking.setFullName(bookingDTO.getFullName());

        var updatedBooking = bookingRepository.save(foundBooking);

        return modelMapper.map(updatedBooking, BookingDTO.class);
    }

    @Override
    @Transactional
    public BookingDTO findBookingById(Long bookingId) throws BookingNotFoundException {
        var booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found!"));
        return modelMapper.map(booking, BookingDTO.class);
    }

    @Override
    @Transactional
    public List<LocalDate> getAvailableDatesForDateRange(LocalDate startDate, LocalDate endDate) {

        if (Objects.isNull(startDate)) {
            startDate = LocalDate.now().plusDays(1);
        }

        if (Objects.isNull(endDate)) {
            endDate = LocalDate.now().plusMonths(1);
        }
        var availableDates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());

        var bookings = bookingRepository.findAllBookingsByDataRange(startDate, endDate);

        bookings.forEach(b -> availableDates.removeAll(b.getAllDatesFromBookingDate()));

        return availableDates;
    }
}
