package org.example.tripbuddy.domain.user.register.controller;


import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.user.register.dto.RegisterRequest;
import org.example.tripbuddy.domain.user.register.service.RegisterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService userService;


    @PostMapping("/api/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(userService.register(request));
    }

}
