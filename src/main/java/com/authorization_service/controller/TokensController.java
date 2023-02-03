package com.authorization_service.controller;

import com.authorization_service.Entity.AccessToken;
import com.authorization_service.Entity.User;
import com.authorization_service.repository.interfaces.AccessTokenRepository;
import com.authorization_service.repository.interfaces.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tokens")
public class TokensController {
    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;

    public TokensController(UserRepository userRepository, AccessTokenRepository accessTokenRepository) {
        this.userRepository = userRepository;
        this.accessTokenRepository = accessTokenRepository;
    }

    @PostMapping
    public ResponseEntity<AccessToken> create() {
        User user = userRepository.getById("2c964dbf86186f230186186fcb110000");

        AccessToken accessToken = new AccessToken();
        accessToken.setAccess_token("222-fjfjjf");
        accessToken.setExpires_in(3600);
        accessToken.setToken_type("Bearer");
        accessToken.setUser(user);


        Optional<User> optionalUser = userRepository.findById(accessToken.getUser().getId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        accessToken.setUser(optionalUser.get());

        AccessToken savedAccessToken = accessTokenRepository.save(accessToken);

        return ResponseEntity.ok(savedAccessToken);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccessToken> update(@PathVariable String id) {
        AccessToken new_at = new AccessToken();
        new_at.setExpires_in(4000);

        new_at.setUser(accessTokenRepository.getById(id).getUser());

        Optional<User> optionalUser = userRepository.findById(new_at.getUser().getId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        Optional<AccessToken> optionalAccessToken = accessTokenRepository.findById(id);
        if (optionalAccessToken.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        new_at.setUser(optionalUser.get());
        new_at.setAccess_token(optionalAccessToken.get().getAccess_token());
        new_at.setToken_type(optionalAccessToken.get().getToken_type());
        accessTokenRepository.save(new_at);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AccessToken> delete(@PathVariable String id) {
        Optional<AccessToken> optionalAccessToken = accessTokenRepository.findById(id);
        if (optionalAccessToken.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        accessTokenRepository.delete(optionalAccessToken.get());

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AccessToken>> getAll() {
        return ResponseEntity.ok(accessTokenRepository.findAll());
    }
}
