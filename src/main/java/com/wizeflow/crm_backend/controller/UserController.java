package com.wizeflow.crm_backend.controller;

import com.wizeflow.crm_backend.infrastructure.entity.User;
import com.wizeflow.crm_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuario")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> saveUser(@RequestBody User user) {
        userService.saveUser(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.searchUserByEmail(email));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserByEmail(@RequestParam String email) {
        userService.deleteByEmail(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateUserByEmail(@RequestBody User user) {
        userService.updateUserByEmail(user.getEmail(), user);
        return ResponseEntity.ok().build();
    }
}
