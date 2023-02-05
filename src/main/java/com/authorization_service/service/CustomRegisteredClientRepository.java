package com.authorization_service.service;

import com.authorization_service.Entity.Application;
import com.authorization_service.repository.interfaces.AppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(RegisteredClient registeredClient) {
        System.out.println("Save");
    }

    @Override
    public RegisteredClient findById(String id) {
        return findByClientId(id);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Application app = appRepository.getByClient_id(clientId);
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(bCryptPasswordEncoder.encode(app.getClient_secret()))
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .scope(OidcScopes.OPENID)
                .redirectUri(app.getRedirect_uri())
                .build();
    }
}
