package com.abciloveu.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.List;

@RestController
public class AdminController {

    @GetMapping("/admin/configs")
    @Secured({ "ROLE_ADMIN" })
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EDITOR')")
    public ResponseEntity<List<String>> configs() {
        return ResponseEntity.ok(Arrays.asList("config1", "config2"));
    }

    @PostMapping(value = "/admin/configs")
    @Secured({ "ROLE_ADMIN" })
    public String editApp(@RequestBody String loginRequest) {

        return loginRequest;
    }

}
