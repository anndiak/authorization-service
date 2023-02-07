package com.authorization_service.repository.interfaces;

import com.authorization_service.Entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, String> {

    Session getByCode(String code);
}
