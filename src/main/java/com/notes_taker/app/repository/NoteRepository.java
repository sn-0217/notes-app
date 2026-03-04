package com.notes_taker.app.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.notes_taker.app.entity.Note;
import com.notes_taker.app.entity.User;

public interface NoteRepository extends JpaRepository<Note, Long>{
    Optional<Note> findByTitleAndUser(String title, User user);
    void deleteByTitleAndUser(String title, User user);
    List<Note> findByUser(User user);
}
