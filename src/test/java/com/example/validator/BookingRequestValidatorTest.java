package com.example.validator;

import com.example.model.BookingRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookingRequestValidatorTest {
    @Test
    public void testIsValid_validBookingRequest_success() {
        BookingRequest request = new BookingRequest(
                "Alice",
                2,
                LocalDate.now().plusDays(1).toString(),
                "12:00",
                "123-456-7890"
        );

        assertDoesNotThrow(() -> BookingRequestValidator.isValid(request));
    }

    @Test
    public void testIsValid_nullBookingRequest_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> BookingRequestValidator.isValid(null));
    }

    @Test
    public void testIsValid_emptyName_throwsException() {
        BookingRequest request = new BookingRequest(
                "   ",
                2,
                LocalDate.now().plusDays(1).toString(),
                "12:00",
                "123-456-7890"
        );

        assertThrows(IllegalArgumentException.class, () -> BookingRequestValidator.isValid(request));
    }

    @Test
    public void testIsValid_invalidTableSize_throwsException() {
        BookingRequest request = new BookingRequest(
                "Alice",
                -1,
                LocalDate.now().plusDays(1).toString(),
                "12:00",
                "123-456-7890"
        );

        assertThrows(IllegalArgumentException.class, () -> BookingRequestValidator.isValid(request));
    }

    @Test
    public void testIsValid_emptyDate_throwsException() {
        BookingRequest request = new BookingRequest(
                "Alice",
                2,
                "   ",
                "12:00",
                "123-456-7890"
        );

        assertThrows(IllegalArgumentException.class, () -> BookingRequestValidator.isValid(request));
    }

    @Test
    public void testIsValid_invalidDateFormat_throwsException() {
        BookingRequest request = new BookingRequest(
                "Alice",
                2,
                "07/04/2025",
                "12:00",
                "123-456-7890"
        );

        assertThrows(IllegalArgumentException.class, () -> BookingRequestValidator.isValid(request));
    }

    @Test
    public void testIsValid_pastDate_throwsException() {
        BookingRequest request = new BookingRequest(
                "Alice",
                2,
                LocalDate.now().minusDays(1).toString(),
                "12:00",
                "123-456-7890"
        );

        assertThrows(IllegalArgumentException.class, () -> BookingRequestValidator.isValid(request));
    }

    @Test
    public void testIsValid_invalidTimeFormat_throwsException() {
        BookingRequest request = new BookingRequest(
                "Alice",
                2,
                LocalDate.now().plusDays(1).toString(),
                "noon",
                "123-456-7890"
        );

        assertThrows(IllegalArgumentException.class, () -> BookingRequestValidator.isValid(request));
    }

    @Test
    public void testIsValid_missingCustomerTel_throwsException() {
        BookingRequest request = new BookingRequest(
                "Alice",
                2,
                LocalDate.now().plusDays(1).toString(),
                "12:00",
                "   "
        );

        assertThrows(IllegalArgumentException.class, () -> BookingRequestValidator.isValid(request));
    }

    @Test
    public void testIsValid_multipleValidationErrors_throwsException() {
        BookingRequest request = new BookingRequest(
                "",
                -1,
                LocalDate.now().minusDays(1).toString(),
                "13pm",
                null
        );

        assertThrows(IllegalArgumentException.class, () -> BookingRequestValidator.isValid(request));
    }
}