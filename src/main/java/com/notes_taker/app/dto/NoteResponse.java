package com.notes_taker.app.dto;

import java.time.Instant;

import lombok.Builder;

@Builder
public record NoteResponse(Long id, String title, String content, Instant createdAt, Instant modifiedAt) {}