package com.authorization_service.controller;

import com.authorization_service.Entity.Role;
import com.authorization_service.Entity.Scope;
import com.authorization_service.repository.interfaces.RoleRepository;
import com.authorization_service.repository.interfaces.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ScopeRepository scopeRepository;

    @GetMapping(value = "roles")
    public ResponseEntity<List<Role>> getAllRoles(){
        List<Role> roles = roleRepository.getAll();
        return ResponseEntity.ok(roles);
    }

    public Role createRoles(){
        Role role = new Role();
        role.setRole("Admin");
        return role;
    }

    @GetMapping(value = "scopes")
    public ResponseEntity<List<Scope>> getAllScopes(){
        List<Scope> scopes = scopeRepository.getAll();
        return ResponseEntity.ok(scopes);
    }
}
