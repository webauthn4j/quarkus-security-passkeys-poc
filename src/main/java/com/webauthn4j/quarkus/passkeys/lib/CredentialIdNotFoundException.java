package com.webauthn4j.quarkus.passkeys.lib;

public class CredentialIdNotFoundException extends RuntimeException{
    public CredentialIdNotFoundException(String message) {
        super(message);
    }
}
