package com.example.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Booking(
        UUID id,
        String name,
        int tableSize,
        LocalDateTime dateTime,
        String customerTel
) { }
