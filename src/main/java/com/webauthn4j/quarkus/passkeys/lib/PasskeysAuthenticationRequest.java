package com.webauthn4j.quarkus.passkeys.lib;

import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.server.ServerProperty;
import io.quarkus.security.identity.request.BaseAuthenticationRequest;

import java.util.List;
import java.util.Objects;

public class PasskeysAuthenticationRequest extends BaseAuthenticationRequest {

    private final String username;

    private final String authenticationResponseJSON;

    private final ServerProperty serverProperty;
    private final CredentialRecord credentialRecord;
    private final List<byte[]> allowCredentials;
    private final boolean userVerificationRequired;
    private final boolean userPresenceRequired;

    public PasskeysAuthenticationRequest(String username, String authenticationResponseJSON, ServerProperty serverProperty, CredentialRecord credentialRecord, List<byte[]> allowCredentials, boolean userVerificationRequired, boolean userPresenceRequired) {
        this.username = username;
        this.authenticationResponseJSON = authenticationResponseJSON;
        this.serverProperty = serverProperty;
        this.credentialRecord = credentialRecord;
        this.allowCredentials = allowCredentials;
        this.userVerificationRequired = userVerificationRequired;
        this.userPresenceRequired = userPresenceRequired;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthenticationResponseJSON() {
        return authenticationResponseJSON;
    }

    public ServerProperty getServerProperty() {
        return serverProperty;
    }

    public CredentialRecord getCredentialRecord() {
        return credentialRecord;
    }

    public List<byte[]> getAllowCredentials() {
        return allowCredentials;
    }

    public boolean isUserVerificationRequired() {
        return userVerificationRequired;
    }

    public boolean isUserPresenceRequired() {
        return userPresenceRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasskeysAuthenticationRequest that = (PasskeysAuthenticationRequest) o;
        return userVerificationRequired == that.userVerificationRequired && userPresenceRequired == that.userPresenceRequired && Objects.equals(username, that.username) && Objects.equals(authenticationResponseJSON, that.authenticationResponseJSON) && Objects.equals(serverProperty, that.serverProperty) && Objects.equals(credentialRecord, that.credentialRecord) && Objects.equals(allowCredentials, that.allowCredentials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authenticationResponseJSON, serverProperty, credentialRecord, allowCredentials, userVerificationRequired, userPresenceRequired);
    }
}
