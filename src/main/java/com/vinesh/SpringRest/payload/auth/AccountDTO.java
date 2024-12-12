package com.vinesh.SpringRest.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {
    private String name;
    @Email
    @Schema(description = "Email address", example = "abc@gmail.com", requiredMode = RequiredMode.REQUIRED)
    private String email;
    @Size(min = 6, max = 20)
    @Schema(description = "Password", example = "password", requiredMode = RequiredMode.REQUIRED)
    private String password;
}
