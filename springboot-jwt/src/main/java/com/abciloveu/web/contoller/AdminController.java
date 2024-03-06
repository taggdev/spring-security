package com.abciloveu.web.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class AdminController {

    @GetMapping("/admin/configs")
    public ResponseEntity<List<String>> configs() {
        return ResponseEntity.ok(Arrays.asList("config1", "config2"));
    }
}
