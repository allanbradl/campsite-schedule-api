package com.bernardoms.campsitescheduleapi.unit.service;

import com.bernardoms.campsitescheduleapi.dto.BookingDTO;
import com.bernardoms.campsitescheduleapi.exception.BookingDateUnavailableException;
import com.bernardoms.campsitescheduleapi.exception.BookingNotFoundException;
import com.bernardoms.campsitescheduleapi.exception.IllegalBookingStateException;
import com.bernardoms.campsitescheduleapi.model.Booking;
import com.bernardoms.campsitescheduleapi.model.BookingStatus;
import com.bernardoms.campsitescheduleapi.repository.BookingRepository;
import com.bernardoms.campsitescheduleapi.service.BookingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    @Test
    void shouldReturnAvailabilityDaysStartDateNullAndNoConflictBookings() {
        var endDate = LocalDate.now().plusDays(3);
        var tomorrowDate = LocalDate.now().plusDays(1);

        when(bookingRepository.findAllBookingsByDataRange(any(), eq(endDate))).thenReturn(Collections.emptyList());

        var availableDatesForDateRange = bookingService.getAvailableDatesForDateRange(null, endDate);

        assertEquals(3, availableDatesForDateRange.size());
        assertEquals(tomorrowDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList()), availableDatesForDateRange);
    }

    @Test
    void shouldReturnAvailabilityDaysEndDateNullAndNoConflictBookings() {
        var now = LocalDate.now().plusDays(1);

        when(bookingRepository.findAllBookingsByDataRange(eq(now), eq(LocalDate.now().plusMonths(1)))).thenReturn(Collections.emptyList());

        var availableDatesForDateRange = bookingService.getAvailableDatesForDateRange(now, null);

        assertEquals(31, availableDatesForDateRange.size());
        assertEquals(now.datesUntil(LocalDate.now().plusDays(1).plusMonths(1)).collect(Collectors.toList()), availableDatesForDateRange);
    }

    @Test
    void shouldReturnAvailabilityDaysConflictingBooking() {

        LocalDate now = LocalDate.now();

        var booking = Booking.builder().startDate(now.plusDays(5)).endDate(now.plusDays(7)).build();
        var booking2 = Booking.builder().startDate(now.plusDays(8)).endDate(now.plusDays(10)).build();

        var availableDateRange = LocalDate.now().plusDays(1).datesUntil(LocalDate.now().plusDays(5)).collect(Collectors.toList());
        var availableDateRange2 = LocalDate.now().plusDays(7).datesUntil(LocalDate.now().plusDays(8)).collect(Collectors.toList());

        List<LocalDate> availableDateRange3 = LocalDate.now().plusDays(10).datesUntil(LocalDate.now().plusDays(30)).collect(Collectors.toList());

        var availableDates = new ArrayList<>();

        availableDates.addAll(availableDateRange);
        availableDates.addAll(availableDateRange2);
        availableDates.addAll(availableDateRange3);

        when(bookingRepository.findAllBookingsByDataRange(any(), any())).thenReturn(Arrays.asList(booking, booking2));
        var availableDatesForDateRange = bookingService.getAvailableDatesForDateRange(LocalDate.now().plusDays(1), LocalDate.now().plusDays(29));

        assertEquals(25, availableDatesForDateRange.size());
        assertEquals(availableDates, availableDatesForDateRange);
    }

    @Test
    public void shouldCreateBookingWithSuccess() throws BookingDateUnavailableException {
        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(10))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .fullName("test test")
                .build();

        var booking = Booking.builder()
                .endDate(LocalDate.now().plusDays(10))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .id(1L)
                .fullName("test test").build();

        when(modelMapper.map(bookingDTO, Booking.class)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);

        Long bookingId = bookingService.createBooking(bookingDTO).getId();

        verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());

        assertEquals(1L, bookingId);
    }

    @Test
    public void shouldThrowBookingDateUnavailableWhenThereIsDateConflict() {
        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .fullName("test test")
                .build();

        var booking = Booking.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .id(1L)
                .fullName("test test").build();

        var foundBooking = Booking.builder()
                .endDate(LocalDate.now().plusDays(10))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .id(1L)
                .fullName("test test").build();

        when(modelMapper.map(bookingDTO, Booking.class)).thenReturn(booking);
        when(bookingRepository.findAllBookingsByDataRange(any(), any())).thenReturn(Collections.singletonList(foundBooking));

        var exception = assertThrows(BookingDateUnavailableException.class, () -> {
            bookingService.createBooking(bookingDTO);
            verify(bookingRepository, never()).save(any(Booking.class));
        });

        assertEquals("no availability for " + LocalDate.now().plusDays(7) + " to " + LocalDate.now().plusDays(9), exception.getMessage());
    }

    @Test
    void shouldReturnBookingWhenFoundBookingById() throws BookingNotFoundException {
        var booking = Booking.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .id(1L)
                .fullName("test test").build();

        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .fullName("test test")
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(modelMapper.map(booking, BookingDTO.class)).thenReturn(bookingDTO);

        var foundBooking = bookingService.findBookingById(1L);

        assertEquals(bookingDTO.getStartDate(), foundBooking.getStartDate());
        assertEquals(bookingDTO.getEndDate(), foundBooking.getEndDate());
        assertEquals(bookingDTO.getEmail(), foundBooking.getEmail());
        assertEquals(bookingDTO.getFullName(), foundBooking.getFullName());
    }

    @Test
    void shouldThrowBookingNotFound() {
        var exception = assertThrows(BookingNotFoundException.class, () -> {
            bookingService.findBookingById(1L);
            verify(modelMapper, never()).map(any(), any());
        });

        assertEquals("Booking with id 1 not found!", exception.getMessage());
    }

    @Test
    void shouldCancelBooking() throws BookingNotFoundException {
        var booking = Booking.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .id(1L)
                .fullName("test test").build();

        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .fullName("test test")
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(modelMapper.map(booking, BookingDTO.class)).thenReturn(bookingDTO);
        when(modelMapper.map(bookingDTO, Booking.class)).thenReturn(booking);


       bookingService.cancelBooking(1L);

       verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());
       assertEquals(BookingStatus.CANCELED, bookingArgumentCaptor.getValue().getStatus());
    }

    @Test
    void shouldThrowBookingNotFoundWhenCanceling() {
        var exception = assertThrows(BookingNotFoundException.class, () -> {
            bookingService.cancelBooking(1L);
            verify(modelMapper, never()).map(any(), any());
        });

        assertEquals("Booking with id 1 not found!", exception.getMessage());
    }

    @Test
    void shouldUpdateBookingWithSuccess() throws BookingNotFoundException, BookingDateUnavailableException, IllegalBookingStateException {
        var booking = Booking.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .id(1L)
                .fullName("test test").build();

        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(7))
                .startDate(LocalDate.now().plusDays(5))
                .email("updated@test.com")
                .fullName("test updated")
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(modelMapper.map(any(), eq(BookingDTO.class))).thenReturn(bookingDTO);

        var updateBooking = bookingService.updateBooking(1L, bookingDTO);

        verify(bookingRepository, times(1)).save(any());

        assertEquals(bookingDTO.getEndDate(), updateBooking.getEndDate());
        assertEquals(bookingDTO.getFullName(), updateBooking.getFullName());
        assertEquals(bookingDTO.getEmail(), updateBooking.getEmail());
        assertEquals(bookingDTO.getStartDate(), updateBooking.getStartDate());
    }

    @Test
    void shouldThrowNoBookingFoundWhenTryToUpdateANonExistentBooking()  {
        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(7))
                .startDate(LocalDate.now().plusDays(5))
                .email("updated@test.com")
                .fullName("test updated")
                .build();

        var exception = assertThrows(BookingNotFoundException.class, () -> {
            bookingService.updateBooking(1L, bookingDTO);
            verify(modelMapper, never()).map(any(), any());
        });

        assertEquals("Booking with id 1 not found!", exception.getMessage());
    }

    @Test
    void shouldThrowIllegalBookingStateExceptionWhenTryToUpdateACanceledBooking() {
        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(7))
                .startDate(LocalDate.now().plusDays(5))
                .email("updated@test.com")
                .fullName("test updated")
                .build();

        var booking = Booking.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .id(1L)
                .status(BookingStatus.CANCELED)
                .fullName("test test").build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        var exception = assertThrows(IllegalBookingStateException.class, () -> {
            bookingService.updateBooking(1L, bookingDTO);
            verify(modelMapper, never()).map(any(), any());
        });

        assertEquals("cant update a canceled booking", exception.getMessage());
    }

    @Test
    void shouldThrowBookingDateUnavailableExceptionWhenTryToUpdateBookingForANotAavailableDate() {
        var booking = Booking.builder()
                .endDate(LocalDate.now().plusDays(9))
                .startDate(LocalDate.now().plusDays(7))
                .email("test@test.com")
                .id(1L)
                .fullName("test test").build();

        var bookingDTO = BookingDTO.builder()
                .endDate(LocalDate.now().plusDays(7))
                .startDate(LocalDate.now().plusDays(5))
                .email("updated@test.com")
                .fullName("test updated")
                .build();

        var foundBooking = Booking.builder()
                .endDate(LocalDate.now().plusDays(6))
                .startDate(LocalDate.now().plusDays(5))
                .email("test@test.com")
                .id(1L)
                .fullName("test test").build();


        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.findAllBookingsByDataRange(any(), any())).thenReturn(Collections.singletonList(foundBooking));

        var exception = assertThrows(BookingDateUnavailableException.class, () -> {
            bookingService.updateBooking(1L, bookingDTO);
            verify(modelMapper, never()).map(any(), any());
            verify(bookingRepository, never()).save(any());
        });

        assertEquals("no availability for " + LocalDate.now().plusDays(5) + " to " + LocalDate.now().plusDays(7), exception.getMessage());
    }
}
