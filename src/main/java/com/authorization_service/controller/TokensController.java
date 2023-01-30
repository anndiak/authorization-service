package com.authorization_service.controller;

import com.authorization_service.Entity.AccessToken;
import com.authorization_service.Entity.User;
import com.authorization_service.repository.interfaces.AccessTokenRepository;
import com.authorization_service.repository.interfaces.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
        User user = userRepository.getById("2222-hvhhv");

        AccessToken accessToken = new AccessToken();
        accessToken.setAccess_token("111-fjfjjf");
        accessToken.setExpires_in(3600);
        accessToken.setToken_type("Bearer");
        accessToken.setUser(user);


        Optional<User> optionalUser = userRepository.findById(accessToken.getUser().getId());
        if (!optionalUser.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        accessToken.setUser(optionalUser.get());

        AccessToken savedAccessToken = accessTokenRepository.save(accessToken);

        return ResponseEntity.ok(savedAccessToken);
    }

    @GetMapping
    public ResponseEntity<List<AccessToken>> getAll() {
        return ResponseEntity.ok(accessTokenRepository.findAll());
    }
}
