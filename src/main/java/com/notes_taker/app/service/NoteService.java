package com.notes_taker.app.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.notes_taker.app.dto.NoteRequest;
import com.notes_taker.app.dto.NoteResponse;
import com.notes_taker.app.entity.Note;
import com.notes_taker.app.exeption.NotesAlreadyExistsException;
import com.notes_taker.app.exeption.NotesNotFoundException;
import com.notes_taker.app.mapper.NoteMapper;
import com.notes_taker.app.repository.NoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    public NoteResponse saveNotes(NoteRequest noteRequest) {
        if(noteRepository.findByTitle(noteRequest.title()).orElse(null) == null){
            Note note = noteMapper.toEntity(noteRequest);
            Instant now = Instant.now();
            note.setCreatedAt(now);
            note.setModifiedAt(now);
    
            Note saved = noteRepository.save(note);
            return noteMapper.toDto(saved);
        }
        throw new NotesAlreadyExistsException("Notes with title " + noteRequest.title() + " already exists");
    }

    public List<NoteResponse> getAllNotes(){
        List<NoteResponse> notes = noteRepository.findAll().stream().map(note -> noteMapper.toDto(note)).collect(Collectors.toList());
        if(notes == null || notes.isEmpty()) {
            throw new NotesNotFoundException("Notes is either empty or null");
        }
        return notes;
    }

}
