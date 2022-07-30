package com.decagon.bank.controllers;

import com.decagon.bank.dtos.request.SignUpDto;
import com.decagon.bank.dtos.response.User;
import com.decagon.bank.dtos.response.UserNotFoundException;
import com.decagon.bank.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("users")
public class UserController {

    private final UserService mService;

    @Async
    @CrossOrigin
    @PostMapping("/signup")
    @ApiOperation(value = "::: createUser :::", notes = "API for user creation with login credentials")
    public CompletableFuture<ResponseEntity<User>> createUser(@RequestBody SignUpDto signUpDto)
            throws UserNotFoundException {

        final User user = mService.registerUser(signUpDto);

        return CompletableFuture.completedFuture(new ResponseEntity<>(user, HttpStatus.CREATED));
    }


}
