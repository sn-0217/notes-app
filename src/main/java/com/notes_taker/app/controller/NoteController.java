package com.notes_taker.app.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.notes_taker.app.dto.NoteRequest;
import com.notes_taker.app.dto.NoteResponse;
import com.notes_taker.app.entity.User;
import com.notes_taker.app.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest noteRequest, @AuthenticationPrincipal User user){
        NoteResponse noteResponse = noteService.saveNotes(noteRequest, user);
        return new ResponseEntity<>(noteResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAllNotes(@AuthenticationPrincipal User user){
        List<NoteResponse> notes = noteService.getAllNotes(user);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{title}")
    public ResponseEntity<NoteResponse> getNotesByTitle(@PathVariable String title, @AuthenticationPrincipal User user){
        NoteResponse noteResponse = noteService.getNotesByTitle(title, user);
        return noteResponse == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(noteResponse);
    }

    @PatchMapping("/{title}/archive")
    public ResponseEntity<String> archiveNotes(@PathVariable String title, @AuthenticationPrincipal User user){
        noteService.archiveNotes(title, user);
        return ResponseEntity.ok(title + " archived");
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<String> deleteNote(@PathVariable String title, @AuthenticationPrincipal User user){
        noteService.deleteNoteByTitle(title, user);
        return ResponseEntity.ok(title + " deleted");
    }

    @PutMapping
    public ResponseEntity<NoteResponse> updateNote(@Valid @RequestBody NoteRequest noteRequest, @AuthenticationPrincipal User user){
        NoteResponse noteResponse = noteService.updateNote(noteRequest, user);
        return ResponseEntity.ok(noteResponse);
    }
}
