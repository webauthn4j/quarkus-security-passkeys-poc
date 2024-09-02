package com.webauthn4j.quarkus.passkeys.lib;

import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.*;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.extension.client.AuthenticationExtensionsClientInputs;
import com.webauthn4j.quarkus.passkeys.lib.challenge.ChallengeRepository;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import java.util.Collections;

public class PasskeysAttestationOptionsEndpoint {

    private final ChallengeRepository challengeRepository;
    private final ObjectConverter objectConverter = new ObjectConverter();

    public PasskeysAttestationOptionsEndpoint(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    public void handleRequest(RoutingContext context) {
        byte[] userHandle = new byte[32]; //TODO
        String username = "";
        String displayName = "";
        Challenge challenge = challengeRepository.loadOrGenerateChallenge(context);
        PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions = new PublicKeyCredentialCreationOptions(
                new PublicKeyCredentialRpEntity(
                        null,
                        "quarkus-security-passkeys-poc"
                ),
                new PublicKeyCredentialUserEntity(
                        userHandle,
                        username,
                        displayName
                ),
                challenge,
                Collections.singletonList(new PublicKeyCredentialParameters(
                        PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256
                )),
                60L * 1000,
                null, //TODO
                new AuthenticatorSelectionCriteria(
                        null,
                        true,
                        ResidentKeyRequirement.REQUIRED,
                        UserVerificationRequirement.REQUIRED
                ),
                AttestationConveyancePreference.DIRECT,
                new AuthenticationExtensionsClientInputs<>()
        );
        String json = objectConverter.getJsonConverter().writeValueAsString(publicKeyCredentialCreationOptions);
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        context.response().end(json);
    }
}