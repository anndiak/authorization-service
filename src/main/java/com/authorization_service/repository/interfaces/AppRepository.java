package com.authorization_service.repository.interfaces;

import com.authorization_service.Entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRepository extends JpaRepository<Application, String> {
}
