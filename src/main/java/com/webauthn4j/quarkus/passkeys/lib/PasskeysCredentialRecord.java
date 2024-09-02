package com.webauthn4j.quarkus.passkeys.lib;

import com.webauthn4j.credential.CredentialRecord;

public interface PasskeysCredentialRecord extends CredentialRecord {

    String getUsername();
}
