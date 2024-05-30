package com.glabs.payload.request;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
    @Schema(description = "Роль/Роли передаются массивом", example = "[user], [admin], [user, admin]")
    private Set<String> roles;

    private String firstName;

    private String lastName;

    private String phoneNumber;
}
