package com.authorization_service.repository.interfaces;

import com.authorization_service.Entity.Role;
import com.authorization_service.Entity.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScopeRepository extends JpaRepository<Scope, String> {

    @Query("SELECT s FROM Scope s WHERE s.name = ?1")
    Scope getScope(String scope);

    @Query("SELECT s FROM Scope s")
    List<Scope> getAll();
}
