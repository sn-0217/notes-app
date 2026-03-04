package com.notes_taker.app.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.notes_taker.app.dto.NoteRequest;
import com.notes_taker.app.dto.NoteResponse;
import com.notes_taker.app.entity.Note;
import com.notes_taker.app.entity.Status;
import com.notes_taker.app.entity.User;
import com.notes_taker.app.exeption.NotesAlreadyArchivedException;
import com.notes_taker.app.exeption.NotesAlreadyExistsException;
import com.notes_taker.app.exeption.NotesNotFoundException;
import com.notes_taker.app.mapper.NoteMapper;
import com.notes_taker.app.repository.NoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    @Transactional
    public NoteResponse saveNotes(NoteRequest noteRequest, User user) {
        if(noteRepository.findByTitleAndUser(noteRequest.title(), user).orElse(null) == null){
            Note note = noteMapper.toEntity(noteRequest);

            Instant now = Instant.now();
            note.setCreatedAt(now);
            note.setModifiedAt(now);
            note.setUser(user);

            Note saved = noteRepository.save(note);
            return noteMapper.toDto(saved);
        }
        throw new NotesAlreadyExistsException("Notes with title " + noteRequest.title() + " already exists");
    }

    public List<NoteResponse> getAllNotes(User user){
        List<NoteResponse> notes = noteRepository.findByUser(user).stream().map(note -> noteMapper.toDto(note)).collect(Collectors.toList());
        if(notes == null || notes.isEmpty()) {
            throw new NotesNotFoundException("Notes is either empty or null");
        }
        return notes;
    }

    public NoteResponse getNotesByTitle(String title, User user){
        Note found = noteRepository.findByTitleAndUser(title, user).orElse(null);
        if(found == null) throw new NotesNotFoundException(title + " not found");
        return noteMapper.toDto(found);
    }

    public void archiveNotes(String title, User user){
        Note note = noteRepository.findByTitleAndUser(title, user).orElse(null);
        if(note == null) throw new NotesNotFoundException(title + " not found to archive");
        if(note.getStatus() == Status.ARCHIVED) throw new NotesAlreadyArchivedException(title + " notes already archived");
        note.setStatus(Status.ARCHIVED);
        noteRepository.save(note);
    }

    @Transactional
    public void deleteNoteByTitle(String title, User user){
        Note note = noteRepository.findByTitleAndUser(title, user).orElse(null);
        if(note == null) throw new NotesNotFoundException(title + " not found");
        noteRepository.deleteByTitleAndUser(title, user);
    }

    @Transactional
    public NoteResponse updateNote(NoteRequest noteRequest, User user){
        Note note = noteRepository.findByTitleAndUser(noteRequest.title(), user).orElse(null);
        if(note == null) throw new NotesNotFoundException(noteRequest.title() + " not found");
        note.setContent(noteRequest.content());
        note.setModifiedAt(Instant.now());

        Note saved = noteRepository.save(note);
        return noteMapper.toDto(saved);
    }

}
