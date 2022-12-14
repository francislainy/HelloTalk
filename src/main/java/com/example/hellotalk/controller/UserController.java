package com.example.hellotalk.controller;

import com.example.hellotalk.model.user.User;
import com.example.hellotalk.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ht/user")
public class UserController {

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/{userId}", "/{userId}/"})
    public ResponseEntity<Object> getUser(@PathVariable UUID userId) {
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

    @GetMapping({"", "/"})
    public ResponseEntity<Object> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping({"", "/"})
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @PutMapping({"/{userId}", "/{userId}/"})
    public ResponseEntity<Object> updateUser(@PathVariable UUID userId, @RequestBody User user) {
        return new ResponseEntity<>(userService.updateUser(userId, user), HttpStatus.OK);
    }

    @DeleteMapping({"/{userId}", "/{userId}/"})
    public ResponseEntity<Object> deleteUser(@PathVariable UUID userId) throws Exception {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
    }

}
