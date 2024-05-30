package com.glabs.commonControllers.auth;

import com.glabs.entities.user.services.UserService;
import com.glabs.payload.request.ConfirmEmailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Авторизация", description = "Rest контроллер авторизации, регистрации, подтверждения почты")
public class AuthController {

    private final UserService userService;
    @Operation(
            summary = "Авторизация",
            description = "Позволяет авторизировать пользователя. При ответе отправляется jwtToken, который используется в дальнейшем на других страницах с доступом."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "not enabled account - не активирован аккаунт"),
            @ApiResponse(responseCode = "404", description = "no such user"),
            @ApiResponse(responseCode = "401", description = "Bad credentials - неверный пароль"),
            @ApiResponse(responseCode = "200", description = "OK - JWT token")
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                              BindingResult bindingResult) throws BindException {
        return userService.signIn(loginRequest, bindingResult);
    }

    @Operation(
            summary = "Регистрация",
            description = "Позволяет зарегистрировать пользователя. При успешной операции отправляется код подвтерждения на потчу"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Username is already taken - username занят, Email is already taken - email занят"),
            @ApiResponse(responseCode = "200", description = "OK - code send")
                    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest,
                                          BindingResult bindingResult,
                                          UriComponentsBuilder uriComponentsBuilder) throws BindException {
        return userService.createUser(signUpRequest, bindingResult, uriComponentsBuilder);
    }

    @Operation(
            summary = "Подтверждение кода почты"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Данного email и code не существует"),
            @ApiResponse(responseCode = "200", description = "OK - JWT token")
    })
    @PostMapping("/confirmEmail")
    public ResponseEntity<?> confirmEmail(@RequestBody ConfirmEmailRequest confirmEmailRequest,
                                          UriComponentsBuilder uriComponentsBuilder) {
        return userService.confirmEmailToken(confirmEmailRequest.getEmail(), confirmEmailRequest.getCode(), uriComponentsBuilder);
    }

    @Operation(
            summary = "Переотправление кода на почту"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK - code send")
    })
    @PostMapping("/resendEmail")
    public ResponseEntity<?> resendEmailConfirmCode(@RequestBody @Schema(description = "Map with email", example = "{\"email\": \"example@example.com\"}") Map<String, Object> data){
        return userService.resendEmailConfirmCode(data);
    }
}
