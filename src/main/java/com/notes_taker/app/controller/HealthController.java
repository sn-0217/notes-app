package com.notes_taker.app.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/")
    public ResponseEntity<Map<String, String>> health(){
        Map<String, String> health = new HashMap<>();
        health.put("health", "OK..");
        health.put("status at", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss z").format(Instant.now().atZone(ZoneId.of("Asia/Kolkata")
)));
        return ResponseEntity.ok(health);
    }
}
