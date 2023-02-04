package com.authorization_service.repository.interfaces;

import com.authorization_service.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
}
