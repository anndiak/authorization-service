package com.authorization_service.controller;

import com.authorization_service.Entity.User;
import com.authorization_service.repository.interfaces.AccessTokenRepository;
import com.authorization_service.repository.interfaces.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable String id) {


        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        User user = new User();
        user.setEmail("111-fhhfhf@gmail.com");
        user.setPhone("+45576888111");
        user.setPassword("12345-455");
        user.setRole("Student");

        user.setId(optionalUser.get().getId());
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        userRepository.delete(optionalUser.get());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.map(ResponseEntity::ok).orElseGet(()
                -> ResponseEntity.unprocessableEntity().build());

    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
