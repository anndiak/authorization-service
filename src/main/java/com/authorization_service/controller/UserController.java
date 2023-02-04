package com.authorization_service.controller;

import com.authorization_service.Entity.User;
import com.authorization_service.Entity.UserRoles;
import com.authorization_service.repository.interfaces.AccessTokenRepository;
import com.authorization_service.repository.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserRepository userRepository, AccessTokenRepository accessTokenRepository) {
        this.userRepository = userRepository;
        this.accessTokenRepository = accessTokenRepository;
    }

    @PostMapping
    public ResponseEntity<User> create() {

        User savedUser1 = new User();
        savedUser1.setEmail("111-fhhfhf@gmail.com");
        savedUser1.setPhone("+45576888111");
        savedUser1.setPassword(bCryptPasswordEncoder.encode("12345"));
        savedUser1.setRole(UserRoles.ADMIN);

        User savedUser2 = new User();
        savedUser2.setEmail("222-fhhfhf@gmail.com");
        savedUser2.setPhone("+45576888222");
        savedUser2.setPassword(bCryptPasswordEncoder.encode("12345"));
        savedUser2.setRole(UserRoles.STUDENT);

        User savedUser3 = new User();
        savedUser3.setEmail("3333-fhhfhf@gmail.com");
        savedUser3.setPhone("+45576888333");
        savedUser3.setPassword(bCryptPasswordEncoder.encode("12345"));
        savedUser3.setRole(UserRoles.STUDENT);

        userRepository.save(savedUser1);
        userRepository.save(savedUser2);
        userRepository.save(savedUser3);

        return ResponseEntity.ok(savedUser3);
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
        user.setPassword(bCryptPasswordEncoder.encode("12345"));
        user.setRole(UserRoles.STUDENT);

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
