package com.authorization_service.controller;

import com.authorization_service.Entity.User;
import com.authorization_service.repository.interfaces.AccessTokenRepository;
import com.authorization_service.repository.interfaces.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;

    public UserController(UserRepository userRepository, AccessTokenRepository accessTokenRepository) {
        this.userRepository = userRepository;
        this.accessTokenRepository = accessTokenRepository;
    }

    @PostMapping
    public ResponseEntity<User> create() {

        User savedUser1 = new User();
        savedUser1.setId("1111-hvhhv");
        savedUser1.setEmail("111-fhhfhf@gmail.com");
        savedUser1.setPhone("+45576888111");
        savedUser1.setPassword("12345");
        savedUser1.setRole("Student");

        User savedUser2 = new User();
        savedUser2.setId("2222-hvhhv");
        savedUser2.setEmail("222-fhhfhf@gmail.com");
        savedUser2.setPhone("+45576888222");
        savedUser2.setPassword("12345");
        savedUser2.setRole("Student");

        userRepository.save(savedUser1);
        userRepository.save(savedUser2);

        return ResponseEntity.ok(savedUser1);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
