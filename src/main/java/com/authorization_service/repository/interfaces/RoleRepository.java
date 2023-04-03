package com.authorization_service.repository.interfaces;

import com.authorization_service.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, String> {

    @Query("SELECT r FROM Role r WHERE r.role = ?1")
    Role getRole(String role);

    @Query("SELECT r FROM Role r")
    List<Role> getAll();
}
