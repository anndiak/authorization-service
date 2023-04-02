package com.authorization_service.repository.interfaces;

import com.authorization_service.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, String> {

    @Query("SELECT r FROM Role r WHERE r.name = ?1")
    Role getRole(String role);
}
