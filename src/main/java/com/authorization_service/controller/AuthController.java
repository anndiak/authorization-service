package com.authorization_service.controller;

import com.authorization_service.Entity.*;
import com.authorization_service.repository.interfaces.*;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    private Map<String, ?> requestParams;

    private String errorCode, errorMessage, errorName;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ScopeRepository scopeRepository;

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
    public String success_login(Model model) {
        if (isAuthorizationEndpointParametersValid() && checkIfAuthorizedUserCredentialsAreValid()) {

            Session session = createTemporarySession();
            String redirect_uri = requestParams.get("redirect_uri").toString();
            sessionRepository.save(session);
            requestParams.clear();

            return "redirect:" + redirect_uri +"?code=" + session.getCode() + "&state=" + session.getState();
        } else {
            requestParams.clear();

            model.addAttribute("errorName", errorName);
            model.addAttribute("errorCode", errorCode);
            model.addAttribute("errorMessage", errorMessage);
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

                if (session == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such session or it is expired.");
                }

                if (!isGrantTypePresentInApplication(session.getApplication().getClient_id(), grantType)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid grant type.");
                }

                String redirect_uri = (String) body.get("redirect_uri");
                if (StringUtils.isBlank(redirect_uri) || !isRedirectUriPresentInApplication(session.getApplication(), redirect_uri)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid redirect_uri.");
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
                        isGrantTypePresentInApplication(client_id, grantType)) {
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
                        isGrantTypePresentInApplication(body.get("client_id").toString(), grantType)) {
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
                (String)requestParams.get("redirect_uri"),
                (String)requestParams.get("scope"))) {

            buildError("Bad Request", "400", "Fields such as 'response_type', 'client_id', 'redirect_uri', 'scope', 'state' must be specified.");
            return false;
        }

        String responseType = (String)requestParams.get("response_type");
        String client_id = (String)requestParams.get("client_id");
        String redirect_uri = (String)requestParams.get("redirect_uri");
        Application application = appRepository.getByClient_id(client_id);

        if (application == null) {
            buildError("Bad Request", "400", "Invalid client.");
            return false;
        }

        if (!responseType.equals("code")) {
            buildError("Bad Request", "400", "Invalid grant type.");
            return false;
        }

        if (!isRedirectUriPresentInApplication(application, redirect_uri)) {
            buildError("Bad Request", "400", "Invalid Redirect uri.");
            return false;
        }

        return true;
    }

    private boolean isTokenEndpointBodyParametersValid(Map<String,Object> body) {
        if (!StringUtils.isNoneBlank(
                (String)body.get("grant_type"),
                (String)body.get("client_id"),
                (String)body.get("client_secret"))) {
            buildError("Bad Request", "400", "Fields such as 'grant_type', 'client_id' and 'client_secret' must be present.");
            return false; // Bad request
        }

        String grantType = (String)body.get("grant_type");
        if (!(grantType.equals("authorization_code") || grantType.equals("client_credentials") ||
                grantType.equals("password") || grantType.equals("refresh_token"))) {
            buildError("Bad Request", "400", "Invalid grant type.");
            return false;
        }

        return true;
    }

    private Session createTemporarySession() {
        Session session = new Session();
        session.setState(requestParams.get("state").toString());
        session.setCode(UUID.randomUUID().toString());
        session.setApplication(appRepository.getByClient_id(requestParams.get("client_id").toString()));
        session.setUser(userRepository.findByEmail(getAuthenticatedUserEmail()));
        session.setExpires_at(session.getCreated_at().plusSeconds(300));
        Set<Scope> scopeList = getLoginScopes();
        session.setScopes(scopeList);
        return session;
    }

    private Set<Scope> getLoginScopes(){
        String[] scopes = requestParams.get("scope").toString().split(" ");
        Set<Scope> loginScopes = Arrays.stream(scopes).map(s->scopeRepository.getScope(s)).collect(Collectors.toSet());
        loginScopes.remove(null);
        return loginScopes;
    }

    private boolean checkIfAuthorizedUserCredentialsAreValid(){
        Role userRole = userRepository.findByEmail(getAuthenticatedUserEmail()).getRole();
        Set<String> userScopes = userRole.getScopes().stream().map(Scope::getName).collect(Collectors.toSet());
        Set<String> loginProvidedScopes = getLoginScopes().stream().map(Scope::getName).collect(Collectors.toSet());

        if (getLoginScopes().isEmpty()) {
            buildError("Bad Request", "400", "'scope' should be provided.");
        }

        for(String s : loginProvidedScopes) {
            if (!userScopes.contains(s)) {
                buildError("Bad Request", "400", "Invalid scopes.");
                return false;
            }
        }
        return true;
    }

    private String getAuthenticatedUserEmail(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((UserDetails)principal).getUsername();
    }

    private boolean isGrantTypePresentInApplication(String clientId, String grantType) {
        Application application = appRepository.getByClient_id(clientId);
        Set<String> grantTypes = application.getGrantTypes().stream().map(GrantType::getName).collect(Collectors.toSet());
        return grantTypes.contains(grantType);
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

    private void buildError(String name, String code, String message) {
        errorName = name;
        errorCode = code;
        errorMessage = message;
    }

//    @GetMapping("/users")
//    public String users(Model model){
//        List<User> users = userService.findAllUsers();
//        model.addAttribute("users", users);
//        return "users";
//    }
}
