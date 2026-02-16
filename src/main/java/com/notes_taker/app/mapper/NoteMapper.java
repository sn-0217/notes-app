package com.notes_taker.app.mapper;

import org.springframework.stereotype.Component;

import com.notes_taker.app.dto.NoteRequest;
import com.notes_taker.app.dto.NoteResponse;
import com.notes_taker.app.entity.Note;
import com.notes_taker.app.entity.Status;

@Component
public class NoteMapper {
    public Note toEntity(NoteRequest request){
        return Note.builder().title(request.title()).content(request.content()).status(Status.ACTIVE).build();
    }

    public NoteResponse toDto(Note note){
        NoteResponse noteResponse = new NoteResponse(note.getId(), note.getTitle(), note.getContent(), note.getCreatedAt(),  note.getModifiedAt());
        return noteResponse;
    }
}
