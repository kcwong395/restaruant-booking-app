package com.example;

import com.example.adapter.LocalDateTimeAdapter;
import com.example.model.Booking;
import com.example.model.BookingRequest;
import com.example.validator.BookingRequestValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.muserver.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class RestaurantBookingApp {
    private static final int PORT = 8080;
    private static final String V1_API_PREFIX = "v1/bookings";

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private static final List<LocalTime> AVAILABLE_TIMESLOTS = List.of(
            LocalTime.of(10, 0),
            LocalTime.of(12, 0),
            LocalTime.of(14, 0),
            LocalTime.of(16, 0),
            LocalTime.of(18, 0),
            LocalTime.of(20, 0)
    );

    private final List<Booking> BOOKINGS = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        RestaurantBookingApp app = new RestaurantBookingApp();

        MuServer server = MuServerBuilder.httpServer()
                .withHttpPort(PORT)
                .addHandler(Method.GET, V1_API_PREFIX, app::handleGetBookings)
                .addHandler(Method.POST, V1_API_PREFIX, app::handlePostBooking)
                .start();

        System.out.println("Started server at " + server.uri());
    }

    protected void handleGetBookings(MuRequest request, MuResponse response, Map<String, String> pathParams) {
        String date = request.query().get("date");
        if (date == null) {
            response.status(400);
            response.write("Date is required.");
            return;
        }

        try {
            LocalDate validatedDate = LocalDate.parse(date);
            List<Booking> bookings = getBookings(validatedDate);

            response.contentType("application/json");
            response.status(200);
            response.write(GSON.toJson(bookings));
        }
        catch (DateTimeParseException e) {
            response.status(400);
            response.write("Invalid date format.");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            response.status(500);
        }
    }

    protected void handlePostBooking(MuRequest request, MuResponse response, Map<String, String> pathParams) {
        try {
            BookingRequest bookingRequest = GSON.fromJson(request.readBodyAsString(), BookingRequest.class);
            BookingRequestValidator.isValid(bookingRequest);

            LocalDateTime bookingDateTime = LocalDateTime.parse(bookingRequest.date() + "T" + bookingRequest.time());
            List<LocalTime> availableTimeslots = getAvailableTimeslots();
            if (!availableTimeslots.contains(bookingDateTime.toLocalTime())) {
                response.status(400);
                response.write("Required timeslot is not available.");
                return;
            }

            Booking booking = new Booking(
                    UUID.randomUUID(),
                    bookingRequest.name(),
                    bookingRequest.tableSize(),
                    bookingDateTime,
                    bookingRequest.customerTel()
            );
            createBooking(booking);
            System.out.println("Booking created: " + booking);
            response.status(201);
        }
        catch (IOException e) {
            response.status(400);
            response.write("Invalid request body.");
        }
        catch (IllegalArgumentException e) {
            response.status(400);
            response.write(e.getMessage());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            response.status(500);
        }
    }

    protected List<Booking> getBookings(LocalDate date) {
        return BOOKINGS.stream()
                .filter(booking -> booking.dateTime().toLocalDate().equals(date))
                .sorted(Comparator.comparing(Booking::dateTime))
                .toList();
    }

    protected void createBooking(Booking booking) {
        BOOKINGS.add(booking);
    }

    private List<LocalTime> getAvailableTimeslots() {
        return AVAILABLE_TIMESLOTS;
    }
}