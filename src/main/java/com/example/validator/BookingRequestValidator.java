package com.example.validator;

import com.example.model.BookingRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class BookingRequestValidator {
    private static final int MAX_TABLE_SIZE = 10;

    public static void isValid(BookingRequest bookingRequest) throws IllegalArgumentException {
        if (bookingRequest == null) {
            throw new IllegalArgumentException("Booking request is not found.");
        }

        List<String> errors = new ArrayList<>();
        if (bookingRequest.name() == null || bookingRequest.name().trim().isEmpty()) {
            errors.add("Name is required.");
        }

        if (bookingRequest.tableSize() <= 0 || bookingRequest.tableSize() > MAX_TABLE_SIZE) {
            errors.add(String.format("Table size must be greater than 0 and smaller than %d.", MAX_TABLE_SIZE));
        }

        if (bookingRequest.date() == null || bookingRequest.date().trim().isEmpty()) {
            errors.add("Date is required.");
        } else {
            try {
                LocalDate date = LocalDate.parse(bookingRequest.date());
                if (date.isBefore(LocalDate.now().plusDays(1))) {
                    errors.add("Date is invalid.");
                }
            } catch (DateTimeParseException e) {
                errors.add("Invalid date format.");
            }
        }

        if (bookingRequest.time() == null || bookingRequest.time().trim().isEmpty()) {
            errors.add("Time is required.");
        } else {
            try {
                LocalTime.parse(bookingRequest.time());
            } catch (DateTimeParseException e) {
                errors.add("Invalid time format.");
            }
        }

        if (bookingRequest.customerTel() == null || bookingRequest.customerTel().trim().isEmpty()) {
            errors.add("Customer telephone is required.");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }
    }
}
