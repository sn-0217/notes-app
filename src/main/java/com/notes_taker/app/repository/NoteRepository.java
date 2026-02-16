package com.notes_taker.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.notes_taker.app.entity.Note;

public interface NoteRepository extends JpaRepository<Note, Long>{
    Optional<Note> findByTitle(String noteRequest);
    void deleteByTitle(String title);
}
