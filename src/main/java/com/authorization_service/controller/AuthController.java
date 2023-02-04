package com.authorization_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // handler method to handle home page request
    @GetMapping("/index")
    public String home(){
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

//    @GetMapping("/users")
//    public String users(Model model){
//        List<User> users = userService.findAllUsers();
//        model.addAttribute("users", users);
//        return "users";
//    }
}
