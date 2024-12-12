package com.vinesh.SpringRest.payload.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleSignInRequest {
    private String name;
    private String email;
}
