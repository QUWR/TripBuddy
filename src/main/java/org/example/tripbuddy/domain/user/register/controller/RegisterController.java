package org.example.tripbuddy.domain.user.register.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.user.register.dto.RegisterRequest;
import org.example.tripbuddy.domain.user.register.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        registerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
