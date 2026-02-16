package com.notes_taker.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.notes_taker.app.dto.NoteRequest;
import com.notes_taker.app.dto.NoteResponse;
import com.notes_taker.app.service.NoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;
    
    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest noteRequest){
        NoteResponse noteResponse = noteService.saveNotes(noteRequest);
        return new ResponseEntity<>(noteResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAllNotes(){
        List<NoteResponse> notes = noteService.getAllNotes();
        return ResponseEntity.ok(notes);
    }
}
