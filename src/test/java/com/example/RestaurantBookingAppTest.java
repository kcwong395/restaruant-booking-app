package com.example;

import com.example.adapter.LocalDateTimeAdapter;
import com.example.model.Booking;
import com.example.model.BookingRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.muserver.MuRequest;
import io.muserver.MuResponse;
import io.muserver.RequestParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class RestaurantBookingAppTest {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private MuRequest request;
    private MuResponse response;
    private RequestParameters parameters;

    private RestaurantBookingApp app;

    @BeforeEach
    public void setup() {
        request = mock(MuRequest.class);
        response = mock(MuResponse.class);
        parameters = mock(RequestParameters.class);

        app = new RestaurantBookingApp();
    }

    @Test
    public void testPostBooking_invalidJson_return400() throws IOException {
        when(request.readBodyAsString()).thenThrow(new IOException());

        app.handlePostBooking(request, response, Map.of());

        verify(response).status(400);
    }

    @Test
    public void testPostBooking_invalidRequestFields_return400() throws IOException {
        BookingRequest badRequest = new BookingRequest("", -1, "", "", "");
        String json = GSON.toJson(badRequest);
        when(request.readBodyAsString()).thenReturn(json);

        app.handlePostBooking(request, response, Map.of());

        verify(response).status(400);
    }

    @Test
    public void testPostBooking_timeslotUnavailable_return400() throws IOException {
        String date = LocalDate.now().plusDays(1).toString();
        String json = GSON.toJson(new BookingRequest("Bob", 2, date, "13:00", "123-456-7890"));
        when(request.readBodyAsString()).thenReturn(json);

        app.handlePostBooking(request, response, Map.of());

        verify(response).status(400);
    }

    @Test
    public void testPostBooking_success() throws IOException {
        LocalDateTime dateTime = LocalDateTime.parse(LocalDate.now().plusDays(1) + "T" + "12:00");
        String json = GSON.toJson(new BookingRequest(
                "Bob",
                2,
                dateTime.toLocalDate().toString(),
                dateTime.toLocalTime().toString(),
                "123-456-7890")
        );
        when(request.readBodyAsString()).thenReturn(json);

        app.handlePostBooking(request, response, Map.of());
        verify(response).status(201);

        List<Booking> bookings = app.getBookings(LocalDate.now().plusDays(1));
        assertEquals(1, bookings.size());
        assertEquals("Bob", bookings.getFirst().name());
        assertEquals(2, bookings.getFirst().tableSize());
        assertEquals(dateTime, bookings.getFirst().dateTime());
        assertEquals("123-456-7890", bookings.getFirst().customerTel());
    }

    @Test
    public void testGetBookings_missingDate_return400() {
        when(request.query()).thenReturn(parameters);
        when(parameters.get("date")).thenReturn(null);

        app.handleGetBookings(request, response, Map.of());

        verify(response).status(400);
    }

    @Test
    public void testGetBookings_invalidDate_return400() {
        when(request.query()).thenReturn(parameters);
        when(parameters.get("date")).thenReturn("not a date");

        app.handleGetBookings(request, response, Map.of());

        verify(response).status(400);
    }

    @Test
    public void testGetBookings_validDate_success() {
        List<Booking> bookings = List.of(
                new Booking(UUID.randomUUID(), "Tammy", 3, LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(14, 0)), "253-456-7890"),
                new Booking(UUID.randomUUID(), "Bob", 4, LocalDateTime.of(LocalDate.now().plusDays(3), LocalTime.of(12, 0)), "416-456-7890"),
                new Booking(UUID.randomUUID(), "Alice", 2, LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(12, 0)), "123-456-7890")
        );
        for (Booking booking : bookings) {
            app.createBooking(booking);
        }

        when(request.query()).thenReturn(parameters);
        when(parameters.get("date")).thenReturn(LocalDate.now().plusDays(1).toString());

        app.handleGetBookings(request, response, Map.of());

        verify(response).status(200);
        verify(response).contentType("application/json");
        verify(response).write(GSON.toJson(List.of(bookings.get(2), bookings.get(0))));
    }
}