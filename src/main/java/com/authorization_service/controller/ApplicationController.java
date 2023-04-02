package com.authorization_service.controller;

import com.authorization_service.Entity.Application;
import com.authorization_service.Entity.User;
import com.authorization_service.repository.interfaces.AppRepository;
import com.authorization_service.repository.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/apps")
public class ApplicationController {

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private UserRepository userRepository;


    @PostMapping
    public ResponseEntity<Application> create() {

        Application savedApp1 = new Application();
        savedApp1.setClient_id("1111-client_id");
        savedApp1.setClient_secret("1111-client-secret");
        savedApp1.setName("First App");
        savedApp1.setDescription("First App");
        savedApp1.setScope("read");
        savedApp1.setCreated_at(LocalDateTime.now());

        Application savedApp2 = new Application();
        savedApp2.setClient_id("2222-client_id");
        savedApp2.setClient_secret("2222-client-secret");
        savedApp2.setName("Second App");
        savedApp2.setDescription("Second App");
        savedApp1.setScope("read");
        savedApp1.setCreated_at(LocalDateTime.now());

        appRepository.save(savedApp1);
        appRepository.save(savedApp2);

        return ResponseEntity.ok(savedApp1);
    }

    @GetMapping
    public ResponseEntity<List<Application>> getAll() {
        return ResponseEntity.ok(appRepository.findAll());
    }

//    @PostMapping("/assign")
//    public ResponseEntity<User> assignUserToApp() {
//
//        User user1 = userRepository.getById("2c964dbf8619d1bc018619d226750000");
//        User user2 = userRepository.getById("2c964dbf8619d1bc018619d226d20001");
//        User user3 = userRepository.getById("2c964dbf8619d1bc018619d226da0002");
//
//
//        Set<User> users = new HashSet<User>();
//        users.add(user1);
//        users.add(user2);
//
//        Application application1 = appRepository.getById("1111-client_id");
//        Application application2 = appRepository.getById("2222-client_id");
//
//        application1.setUsers(users);
//        application2.setUsers(users);
//
//        application1.addUser(user3);
//
//        appRepository.save(application1);
//        appRepository.save(application1);
//        appRepository.save(application2);
//
//        return ResponseEntity.noContent().build();
//    }
}
