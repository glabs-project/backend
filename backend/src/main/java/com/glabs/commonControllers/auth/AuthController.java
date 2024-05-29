package com.glabs.commonControllers.auth;

import com.glabs.entities.user.services.UserService;
import com.glabs.payload.request.ConfirmEmailRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.glabs.payload.request.LoginRequest;
import com.glabs.payload.request.SignUpRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                              BindingResult bindingResult) throws BindException {
        return userService.signIn(loginRequest, bindingResult);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest,
                                          BindingResult bindingResult,
                                          UriComponentsBuilder uriComponentsBuilder) throws BindException {
        return userService.createUser(signUpRequest, bindingResult, uriComponentsBuilder);
    }

    @PostMapping("/confirmEmail")
    public ResponseEntity<?> confirmEmail(@RequestBody ConfirmEmailRequest confirmEmailRequest,
                                          UriComponentsBuilder uriComponentsBuilder) {
        return userService.confirmEmailToken(confirmEmailRequest.getEmail(), confirmEmailRequest.getCode(), uriComponentsBuilder);
    }

    @PostMapping("/resendEmail")
    public ResponseEntity<?> resendEmailConfirmCode(@RequestBody Map<String, Object> data){
        return userService.resendEmailConfirmCode(data);
    }
}
