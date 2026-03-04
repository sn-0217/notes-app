package com.notes_taker.app.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notes_taker.app.dto.NoteRequest;
import com.notes_taker.app.entity.Note;
import com.notes_taker.app.entity.Status;
import com.notes_taker.app.entity.User;
import com.notes_taker.app.repository.NoteRepository;
import com.notes_taker.app.repository.UserRepository;
import com.notes_taker.app.security.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
public class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String userAToken;
    private User userA;

    private String userBToken;
    private User userB;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
        userRepository.deleteAll();

        // Create User A
        userA = User.builder()
                .username("usera")
                .password(passwordEncoder.encode("password"))
                .build();
        userRepository.save(userA);
        userAToken = jwtService.generateToken(userA);

        // Create User B
        userB = User.builder()
                .username("userb")
                .password(passwordEncoder.encode("password"))
                .build();
        userRepository.save(userB);
        userBToken = jwtService.generateToken(userB);
    }

    @Test
    void createNote_withValidToken_shouldReturnNote() throws Exception {
        NoteRequest request = new NoteRequest("My Test Note", "Test Content");

        mockMvc.perform(post("/api/v1/notes")
                .header("Authorization", "Bearer " + userAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("My Test Note"))
                .andExpect(jsonPath("$.content").value("Test Content"));
    }

    @Test
    void createNote_withoutToken_shouldReturnForbidden() throws Exception {
        NoteRequest request = new NoteRequest("Unauthorized Note", "Content");

        mockMvc.perform(post("/api/v1/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getNotes_shouldOnlyReturnUserNotes() throws Exception {
        // Create notes for User A
        noteRepository.save(Note.builder().title("UserA Note 1").content("C1").user(userA).createdAt(Instant.now()).modifiedAt(Instant.now()).status(Status.ACTIVE).build());
        noteRepository.save(Note.builder().title("UserA Note 2").content("C2").user(userA).createdAt(Instant.now()).modifiedAt(Instant.now()).status(Status.ACTIVE).build());
        // Create note for User B
        noteRepository.save(Note.builder().title("UserB Note 1").content("C3").user(userB).createdAt(Instant.now()).modifiedAt(Instant.now()).status(Status.ACTIVE).build());

        // Fetch User A notes
        mockMvc.perform(get("/api/v1/notes")
                .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("UserA Note 1"))
                .andExpect(jsonPath("$[1].title").value("UserA Note 2"));

        // Fetch User B notes
        mockMvc.perform(get("/api/v1/notes")
                .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("UserB Note 1"));
    }

    @Test
    void getNoteByTitle_shouldReturnNotFound_ifOwnedByOtherUser() throws Exception {
        noteRepository.save(Note.builder()
                .title("Secret")
                .content("B Content")
                .user(userB)
                .createdAt(Instant.now())
                .modifiedAt(Instant.now())
                .status(Status.ACTIVE)
                .build());

        // User A trying to fetch User B's note
        mockMvc.perform(get("/api/v1/notes/Secret")
                .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateNote_shouldUpdateContent() throws Exception {
        noteRepository.save(Note.builder()
                .title("Update Me")
                .content("Old Content")
                .user(userA)
                .createdAt(Instant.now())
                .modifiedAt(Instant.now())
                .status(Status.ACTIVE)
                .build());

        NoteRequest updateRequest = new NoteRequest("Update Me", "New Content");

        mockMvc.perform(put("/api/v1/notes")
                .header("Authorization", "Bearer " + userAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("New Content"));
    }

    @Test
    void deleteNote_shouldRemoveFromDatabase() throws Exception {
        noteRepository.save(Note.builder()
                .title("Delete Me")
                .content("To Delete")
                .user(userA)
                .createdAt(Instant.now())
                .modifiedAt(Instant.now())
                .status(Status.ACTIVE)
                .build());

        mockMvc.perform(delete("/api/v1/notes/Delete Me")
                .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk());

        assertFalse(noteRepository.findByTitleAndUser("Delete Me", userA).isPresent());
    }
}
