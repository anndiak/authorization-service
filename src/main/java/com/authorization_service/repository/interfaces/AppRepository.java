package com.authorization_service.repository.interfaces;

import com.authorization_service.Entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppRepository extends JpaRepository<Application, String> {

    @Query("SELECT a FROM Application a WHERE a.client_id = ?1")
    Application getByClient_id(String client_id);
}
