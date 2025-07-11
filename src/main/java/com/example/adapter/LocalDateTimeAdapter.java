package com.example.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(final LocalDateTime dateTime,
                                 final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        return new JsonPrimitive(dateTime.format(formatter));
    }

    @Override
    public LocalDateTime deserialize(final JsonElement json,
                                     final Type typeOfT,
                                     final JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(), formatter);
    }
}
