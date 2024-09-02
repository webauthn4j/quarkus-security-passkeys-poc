package com.webauthn4j.quarkus.passkeys.lib;

import com.fasterxml.jackson.core.type.TypeReference;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.AuthenticatorAssertionResponse;
import com.webauthn4j.data.PublicKeyCredential;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.extension.client.AuthenticationExtensionClientOutput;
import com.webauthn4j.quarkus.passkeys.lib.challenge.ChallengeRepository;
import com.webauthn4j.server.ServerProperty;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.HttpSecurityUtils;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class PasskeysAuthenticationEndpoint {

    private final IdentityProviderManager identityProviderManager;

    private final PasskeysCredentialRecordService passkeysCredentialRecordService;

    private final PasskeysAuthenticationMechanism passkeysAuthenticationMechanism;

    private final ChallengeRepository challengeRepository;

    private final ObjectConverter objectConverter;

    public PasskeysAuthenticationEndpoint(IdentityProviderManager identityProviderManager, PasskeysCredentialRecordService passkeysCredentialRecordService, PasskeysAuthenticationMechanism passkeysAuthenticationMechanism, ChallengeRepository challengeRepository, ObjectConverter objectConverter) {
        this.identityProviderManager = identityProviderManager;
        this.passkeysCredentialRecordService = passkeysCredentialRecordService;
        this.passkeysAuthenticationMechanism = passkeysAuthenticationMechanism;
        this.challengeRepository = challengeRepository;
        this.objectConverter = objectConverter;
    }

    public void handleRequest(RoutingContext context){

        String body = context.body().asString();
        PublicKeyCredential<AuthenticatorAssertionResponse, AuthenticationExtensionClientOutput> publicKeyCredential = objectConverter.getJsonConverter().readValue(body, new TypeReference<>() {});

        Origin origin = Origin.create(context.request().absoluteURI());
        String rpId = origin.getHost();
        Challenge challenge = challengeRepository.loadChallenge(context);
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);
        List<byte[]> allowCredentials = null; //TODO
        boolean userVerificationRequired = true; //TODO
        boolean userPresenceRequired = true; //TODO

        byte[] credentialId = publicKeyCredential.getRawId();
        passkeysCredentialRecordService.loadCredentialRecordByCredentialId(credentialId).onItem().transformToUni( credentialRecord -> {
            String username = credentialRecord.getUsername();
            PasskeysAuthenticationRequest passkeysAuthenticationRequest = new PasskeysAuthenticationRequest(username, body, serverProperty, credentialRecord, allowCredentials, userVerificationRequired, userPresenceRequired);
            AuthenticationRequest authenticationRequest = HttpSecurityUtils.setRoutingContextAttribute(passkeysAuthenticationRequest, context);
            return identityProviderManager.authenticate(authenticationRequest);
        }).subscribe().with( identity ->{
            passkeysAuthenticationMechanism.getLoginManager().save(identity, context, null, context.request().isSSL());
            context.response().end();
        }, (throwable)->{
            context.fail(500, throwable);
        });
    }
}
