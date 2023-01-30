package com.authorization_service.repository.interfaces;

import com.authorization_service.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, String> {

    User save (User user);
}
