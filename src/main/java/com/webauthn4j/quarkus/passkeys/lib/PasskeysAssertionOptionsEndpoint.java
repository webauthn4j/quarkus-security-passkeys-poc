package com.webauthn4j.quarkus.passkeys.lib;

import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.*;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.quarkus.passkeys.lib.challenge.ChallengeRepository;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

public class PasskeysAssertionOptionsEndpoint {

    private final ChallengeRepository challengeRepository;

    private final ObjectConverter objectConverter = new ObjectConverter();

    public PasskeysAssertionOptionsEndpoint(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    public void handleRequest(RoutingContext context) {
        Challenge challenge = challengeRepository.loadOrGenerateChallenge(context);
        String rpId = null;
        PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions = new PublicKeyCredentialRequestOptions(
                challenge,
                60L * 1000,
                rpId,
                null,
                UserVerificationRequirement.PREFERRED,
                null
        );
        String json = objectConverter.getJsonConverter().writeValueAsString(publicKeyCredentialRequestOptions);
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        context.response().end(json);
    }
}
