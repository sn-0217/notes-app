package com.notes_taker.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notes_taker.app.dto.AuthRequest;
import com.notes_taker.app.dto.RegisterRequest;
import com.notes_taker.app.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        userRepository.deleteAll();
    }

    @Test
    void register_withValidCredentials_shouldReturnToken() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testuser")
                .password("testpass")
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() throws Exception {
        // Register the user first
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testloginuser")
                .password("testloginpass")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Now attempt to login
        AuthRequest loginRequest = AuthRequest.builder()
                .username("testloginuser")
                .password("testloginpass")
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void login_withInvalidPassword_shouldReturnUnauthorizedOrForbidden() throws Exception {
        // Register the user first
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testuser2")
                .password("testpass2")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Attempt to login with wrong password
        AuthRequest loginRequest = AuthRequest.builder()
                .username("testuser2")
                .password("wrongpassword")
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Spring security returns 403 Forbidden by default for failed authentication if 401 isn't explicitly configured.
        response.andExpect(status().isForbidden());
    }
}
