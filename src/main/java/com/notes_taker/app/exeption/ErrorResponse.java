package com.notes_taker.app.exeption;

import java.time.Instant;

import lombok.Builder;

@Builder
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path
) {} 
