package com.authorization_service.controller;

import com.authorization_service.Entity.*;
import com.authorization_service.repository.interfaces.AccessTokenRepository;
import com.authorization_service.repository.interfaces.AppRepository;
import com.authorization_service.repository.interfaces.SessionRepository;
import com.authorization_service.repository.interfaces.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
public class AuthController {

    private Map<String, ?> requestParams;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/oauth2/authorize")
    public String authorize(@RequestParam Map<String, String> reqParam) {
        requestParams = reqParam;
        return "redirect:http://localhost:3000/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/success")
    public String success_login() {
        if (isAuthorizationEndpointParametersValid()) {
            Session session = createTemporarySession();
            String redirect_uri = requestParams.get("redirect_uri").toString();
            sessionRepository.save(session);
            requestParams.clear();
            return "redirect:" + redirect_uri +"?code=" + session.getCode() + "&state=" + session.getState();
        } else {
            requestParams.clear();
            return "error";
        }
    }

    @PostMapping(value="/oauth2/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity token(@RequestBody Map<String,Object> body, @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        if (!isTokenEndpointBodyParametersValid(body)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters");
        }

        String client_id, client_secret;
        String grantType = (String)body.get("grant_type");
        User user = null;

        switch (grantType) {
            case "authorization_code":
                Session session = sessionRepository.getByCode(body.get("code").toString());
                if (session == null || isApplicationGrantTypeValid(session.getApplication().getClient_id(), grantType)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such session or it is expired");
                }

                String redirect_uri = (String) body.get("redirect_uri");
                if (StringUtils.isBlank(redirect_uri) || !isRedirectUriPresentInApplication(session.getApplication(), redirect_uri)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid redirect_uri");
                }

                client_id = session.getApplication().getClient_id();
                client_secret = session.getApplication().getClient_secret();

                if (!client_id.equals(body.get("client_id").toString()) ||
                        !client_secret.equals(body.get("client_secret"))){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid client");
                }

                user = session.getUser();

                break;
            case "client_credentials":
                client_id = body.get("client_id").toString();
                client_secret = body.get("client_secret").toString();
                Application application_cc = appRepository.getByClient_id(client_id);

                if (application_cc == null || !application_cc.getClient_secret().equals(client_secret) ||
                        isApplicationGrantTypeValid(client_id, grantType)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid client");
                }

                break;
            case "password":
                if (!isUserCredentialsValid(body)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid users credentials");
                }
                client_id = body.get("client_id").toString();
                client_secret = body.get("client_secret").toString();
                Application application_ropc = appRepository.getByClient_id(client_id);

                if (application_ropc == null || !application_ropc.getClient_secret().equals(client_secret) ||
                        isApplicationGrantTypeValid(body.get("client_id").toString(), grantType)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid client");
                }

                user = userRepository.findByEmail(body.get("username").toString());

                break;
        }

        AccessToken token = new AccessToken();
        token.setToken_type("Bearer");
        token.setAccess_token(UUID.randomUUID().toString());
        token.setExpires_in(3600);
        token.setUser(user);

        accessTokenRepository.save(token);

        if (grantType.equals("authorization_code")) {
            sessionRepository.delete(sessionRepository.getByCode(body.get("code").toString()));
        }

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    private boolean isAuthorizationEndpointParametersValid() {
        if (!StringUtils.isNoneBlank(
                (String)requestParams.get("state"),
                (String)requestParams.get("response_type"),
                (String)requestParams.get("client_id"),
                (String)requestParams.get("redirect_uri"))) {
            return false; // Bad request
        }

        String responseType = (String)requestParams.get("response_type");

        if (!responseType.equals("code")){
            return false;
        }

        String client_id = (String)requestParams.get("client_id");
        String redirect_uri = (String)requestParams.get("redirect_uri");
        Application application = appRepository.getByClient_id(client_id);
        if ( application == null || !isRedirectUriPresentInApplication(application, redirect_uri)) {
            return false;
        }
        return true;
    }

    private boolean isTokenEndpointBodyParametersValid(Map<String,Object> body) {
        if (!StringUtils.isNoneBlank(
                (String)body.get("grant_type"),
                (String)body.get("client_id"),
                (String)body.get("client_secret"))) {
            return false; // Bad request
        }

        String grantType = (String)body.get("grant_type");
        return grantType.equals("authorization_code") || grantType.equals("client_credentials") ||
                grantType.equals("password") || grantType.equals("refresh_token");
    }

    private Session createTemporarySession() {
        Session session = new Session();
        session.setState(requestParams.get("state").toString());
        session.setCode(UUID.randomUUID().toString());
        session.setApplication(appRepository.getByClient_id(requestParams.get("client_id").toString()));
        session.setUser(userRepository.findByEmail(getAuthenticatedUserEmail()));
        session.setExpires_at(session.getCreated_at().plusSeconds(300));
        return session;
    }

    private String getAuthenticatedUserEmail(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((UserDetails)principal).getUsername();
    }

    private boolean isApplicationGrantTypeValid(String client_id, String grantType){
        Set<GrantType> grantTypes = appRepository.getByClient_id(client_id).getGrantTypes();
        for(GrantType grant : grantTypes) {
            if (grant.getName().equals(grantType)) {
                return false;
            }
        }
        return true;
    }

    private boolean isUserCredentialsValid(Map<String,Object> body) {
        String username = body.get("username").toString();
        String password = body.get("password").toString();
        User user = userRepository.findByEmail(username);
        return StringUtils.isNoneBlank(username, password) &&
                (bCryptPasswordEncoder.matches(password, user.getPassword()));
    }

    private boolean isRedirectUriPresentInApplication(Application application, String redirectUri) {
        Set<RedirectUri> redirectUris = application.getRedirectUris();
        for(RedirectUri uri : redirectUris) {
            if (uri.getName().equals(redirectUri)) {
                return true;
            }
        }
        return false;
    }

//    @GetMapping("/users")
//    public String users(Model model){
//        List<User> users = userService.findAllUsers();
//        model.addAttribute("users", users);
//        return "users";
//    }
}
