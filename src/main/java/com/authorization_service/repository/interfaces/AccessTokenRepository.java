package com.authorization_service.repository.interfaces;

import com.authorization_service.Entity.AccessToken;
import com.authorization_service.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenRepository extends JpaRepository<AccessToken, String> {

}
