package com.bernardoms.campsitescheduleapi.controller;

import com.bernardoms.campsitescheduleapi.exception.BookingDateUnavailableException;
import com.bernardoms.campsitescheduleapi.exception.BookingNotFoundException;
import com.bernardoms.campsitescheduleapi.exception.IllegalBookingStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.net.BindException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    private static final String DESCRIPTION = "description";

    @ExceptionHandler({BindException.class, HttpMessageNotReadableException.class, IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private Object handleIllegalArgumentException(Exception ex, HttpServletRequest request) {
        log.error("invalid arguments/body for processing the request: " + request.getRequestURI(), ex);
        return mountError(ex);
    }

    @ExceptionHandler({BookingDateUnavailableException.class, IllegalBookingStateException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    private Object handleParadoxException(Exception ex, HttpServletRequest request) {
        log.info("conflict exception : {}", ex.getMessage());
        return mountError(ex);
    }

    @ExceptionHandler({BookingNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private Object handleTravelNotFoundException(BookingNotFoundException ex, HttpServletRequest request) {
        log.info("booking not found! : " + request.getRequestURI(), ex);
        return mountError(ex);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Object handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> details = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(field -> details.put(field.getField(), field.getDefaultMessage()));

        ex.getBindingResult().getAllErrors().forEach(field -> details.put(field.getObjectName(), field.getDefaultMessage()));

        log.info("error on the request validation {}", details);

        return Map.of(DESCRIPTION, details);
    }

    private HashMap<Object, Object> mountError(Exception e) {
        var error = new HashMap<>();
        error.put(DESCRIPTION, e.getMessage());
        return error;
    }
}
