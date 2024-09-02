package com.webauthn4j.quarkus.passkeys.lib;

import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.AuthenticatorTransport;
import com.webauthn4j.data.attestation.AttestationObject;
import com.webauthn4j.data.client.CollectedClientData;
import com.webauthn4j.data.extension.client.AuthenticationExtensionsClientOutputs;
import com.webauthn4j.data.extension.client.RegistrationExtensionClientOutput;

import java.util.Set;

public class PasskeysCredentialRecordImpl extends CredentialRecordImpl implements PasskeysCredentialRecord{

    private final String username;

    public PasskeysCredentialRecordImpl(String username, AttestationObject attestationObject, CollectedClientData clientData, AuthenticationExtensionsClientOutputs<RegistrationExtensionClientOutput> clientExtensions, Set<AuthenticatorTransport> transports) {
        super(attestationObject, clientData, clientExtensions, transports);
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
