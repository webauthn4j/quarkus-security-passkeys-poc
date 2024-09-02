package com.webauthn4j.quarkus.passkeys.app;

import jakarta.ws.rs.FormParam;

public class RegisterForm{

    @FormParam("username")
    private String username;

    @FormParam("registrationResponseJSON")
    private String registrationResponseJSON;

    public String getUsername() {
        return username;
    }

    public String getRegistrationResponseJSON() {
        return registrationResponseJSON;
    }
}