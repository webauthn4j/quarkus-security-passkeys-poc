package com.webauthn4j.quarkus.passkeys.lib;

public class PrincipalNotFoundException extends RuntimeException{

    public PrincipalNotFoundException(String message) {
        super(message);
    }
}
