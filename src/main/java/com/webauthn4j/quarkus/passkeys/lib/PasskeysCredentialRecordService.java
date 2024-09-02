package com.webauthn4j.quarkus.passkeys.lib;

import com.webauthn4j.credential.CredentialRecord;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface PasskeysCredentialRecordService {

    /**
     * Updates credential record counter
     *
     * @param credentialId credentialId
     * @param counter      counter
     * @throws CredentialIdNotFoundException if the credential record could not be found
     */
    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    Uni<Void> updateCounter(byte[] credentialId, long counter) throws CredentialIdNotFoundException;

    /**
     * Load {@link CredentialRecord} by credentialId
     * @param credentialId credentialId
     * @return {@link CredentialRecord}
     * @throws CredentialIdNotFoundException if the credential record could not be found
     */
    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    Uni<PasskeysCredentialRecord> loadCredentialRecordByCredentialId(byte[] credentialId) throws CredentialIdNotFoundException;

    /**
     * Load {@link CredentialRecord} list by user principal
     * @param principal user principal
     * @return {@link CredentialRecord} list
     */
    Uni<List<PasskeysCredentialRecord>> loadCredentialRecordsByUserPrincipal(String principal) throws PrincipalNotFoundException;


}
