package com.authorization_service.controller;

import com.authorization_service.Entity.AccessToken;
import com.authorization_service.Entity.Application;
import com.authorization_service.Entity.Session;
import com.authorization_service.Entity.User;
import com.authorization_service.repository.interfaces.AppRepository;
import com.authorization_service.repository.interfaces.SessionRepository;
import com.authorization_service.repository.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private AppRepository appRepository;

    @PostMapping
    public ResponseEntity<Session> create() {

        Application application = appRepository.getById("1111-client_id");

        Session session = new Session();
        session.setCode("11111111111-code");
        session.setExpires_at(session.getCreated_at().plusSeconds(3600));
        session.setApplication(application);

        application.setSessions(List.of(session));

        sessionRepository.save(session);

        return ResponseEntity.ok(session);
    }

    @GetMapping
    public ResponseEntity<List<Session>> getAll() {
        return ResponseEntity.ok(sessionRepository.findAll());
    }

}
