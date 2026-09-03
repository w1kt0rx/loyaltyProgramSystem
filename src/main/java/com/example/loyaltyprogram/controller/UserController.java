package com.example.loyaltyprogram.controller;

import com.example.loyaltyprogram.dto.PageDto;
import com.example.loyaltyprogram.dto.request.CreateUserRequest;
import com.example.loyaltyprogram.dto.request.PageRequestDto;
import com.example.loyaltyprogram.dto.request.UpdateUserRequest;
import com.example.loyaltyprogram.dto.response.UserResponse;
import com.example.loyaltyprogram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping()
    public ResponseEntity<PageDto<UserResponse>> getUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String lastName,
            @ModelAttribute PageRequestDto pageRequest
    ) {
        return ResponseEntity.ok(userService.searchUsers(email, lastName, pageRequest));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/programs/{programId}")
    public ResponseEntity<Void> joinProgram(@PathVariable Long userId, @PathVariable Long programId) {
        userService.joinProgram(userId, programId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/programs/{programId}")
    public ResponseEntity<Void> leaveProgram(@PathVariable Long userId, @PathVariable Long programId) {
        userService.leaveProgram(userId, programId);
        return ResponseEntity.noContent().build();
    }
}
