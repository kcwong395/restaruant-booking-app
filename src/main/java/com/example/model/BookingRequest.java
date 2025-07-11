package com.example.model;

public record BookingRequest(
        String name,
        int tableSize,
        String date,
        String time,
        String customerTel
) { }
