# Restaurant Booking App

A simple RESTful web service for managing restaurant bookings.

## Assumptions
- Bookings can be made for any date in the future (excluded today).
- The restaurant can fulfil unlimited bookings for simplicity (override getAvailableTimeslots method if needed).
- The restaurant operates from 10:00 AM to 10:00 PM (Customers can only book at 10 AM, 12 NN, 14 PM, 16 PM, 18 PM, 20 PM).
- A customer can book multiple tables at the same time.
- Concurrency would be needed, thus ```CopyOnWriteArrayList``` is used
- Table size is limited to 10.

## API Endpoints

### GET `/v1/bookings?date=YYYY-MM-DD`

Retrieve all bookings for the specified date, sorted by time in ascending order.

- **Query Parameters:**
    - `date` (required): The date to filter bookings (format: `YYYY-MM-DD`).

- **Responses:**
    - `200 OK`: Returns an array of bookings for that date.
    - `400 Bad Request`: If the date parameter is missing or invalid.

### POST `/v1/bookings`

Create a new booking.

- **Request Body (JSON):**

```json
{
  "name": "Customer Name",
  "tableSize": 4,
  "date": "YYYY-MM-DD",
  "time": "HH:mm",
  "customerTel": "123-456-7890"
}
```

- **Responses:**
    - `201 Created`: Returns with empty body.
    - `400 Bad Request`: If the booking request is missing or invalid.

## Running the Application

1. Build the project with maven.
2. Run the `RestaurantBookingApp` main class.
3. The server will start on port `8080`.
4. If everything is set up correctly, you should see the log `Started server at http://localhost:8080`.
5. Use an HTTP client (e.g., curl, Postman) to interact with the API.

Example:

Remember to alter the date in the curl command.

Get Bookings
```bash
curl "http://localhost:8080/v1/bookings?date=2025-12-01"
```

Post Booking
```bash
curl -X POST "http://localhost:8080/v1/bookings" \
     -H "Content-Type: application/json" \
     -d '{
           "name": "John Doe",
           "tableSize": 4,
           "date": "2025-12-01",
           "time": "12:00",
           "customerTel": "555-1234-1327"
         }'
```