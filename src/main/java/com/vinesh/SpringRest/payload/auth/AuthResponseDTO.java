package com.vinesh.SpringRest.payload.auth;

import com.vinesh.SpringRest.model.Account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String name;
    private String email;

}
