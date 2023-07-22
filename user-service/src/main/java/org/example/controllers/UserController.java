package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.dtos.CreateUserRequest;
import org.example.dtos.GetUserResponse;
import org.example.models.User;
import org.example.models.UserStatus;
import org.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    // This is the signup api for new user, so this can be unsecured.
    @PostMapping("/user/signup")
    public UserStatus createUser(@RequestBody @Valid CreateUserRequest createUserRequest) throws JsonProcessingException {
        return userService.createIfNotPresent(createUserRequest.toUser());
    }

    @GetMapping("/user")
    public GetUserResponse getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        user = userService.get(user.getId());

        return GetUserResponse.builder()
                .name(user.getName())
                .authorities(user.getAuthority())
                .email(user.getEmail())
                .age(user.getAge())
                .phone(user.getPhone())
                .build();
    }

    // This API can be called by any other service
    @GetMapping(value = "/user/phone/{phone}",produces = MediaType.APPLICATION_JSON_VALUE)
    public GetUserResponse getUserByPhone(@PathVariable("phone") String phone) throws Exception {
        User user = userService.getByPhone(phone);
        return GetUserResponse.builder()
                .name(user.getName())
                .authorities(user.getAuthority())
                .password(user.getPassword())
                .email(user.getEmail())
                .age(user.getAge())
                .phone(user.getPhone())
                .build();
    }

}
