package com.notes_taker.app.dto;

import jakarta.validation.constraints.NotBlank;

//note request to save dto to entity
public record NoteRequest(
    @NotBlank(message = "titile must not be blank")
    String title, 
    
    @NotBlank(message = "Content must not be blank")
    String content) {}
