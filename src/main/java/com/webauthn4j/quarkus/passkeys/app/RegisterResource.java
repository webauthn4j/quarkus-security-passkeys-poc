package com.webauthn4j.quarkus.passkeys.app;

import com.webauthn4j.async.WebAuthnRegistrationAsyncManager;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.quarkus.passkeys.lib.PasskeysCredentialRecord;
import com.webauthn4j.quarkus.passkeys.lib.PasskeysCredentialRecordImpl;
import com.webauthn4j.quarkus.passkeys.lib.challenge.ChallengeRepository;
import com.webauthn4j.server.ServerProperty;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.concurrent.CompletionStage;

@Path("")
public class RegisterResource {

    @Inject
    WebAuthnRegistrationAsyncManager webAuthnRegistrationAsyncManager;

    @Inject
    ChallengeRepository challengeRepository;

    @Inject
    InMemoryPasskeysCredentialRecordService inMemoryPasskeysCredentialRecordService;

    @Path("/register")
    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<Response> register(@BeanParam RegisterForm registerForm, @Context RoutingContext routingContext){

        String registrationResponseJSON = registerForm.getRegistrationResponseJSON();

        Origin origin = Origin.create(routingContext.request().absoluteURI());
        String rpId = origin.getHost();
        Challenge challenge = challengeRepository.loadChallenge(routingContext);
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);
        RegistrationParameters registrationParameters = new RegistrationParameters(
                serverProperty,
                null,
                true,
                true
        );
        CompletionStage<Response> response = webAuthnRegistrationAsyncManager.verify(registrationResponseJSON, registrationParameters)
                .thenCompose(registrationData -> {
                    PasskeysCredentialRecord credentialRecord = new PasskeysCredentialRecordImpl(registerForm.getUsername(), registrationData.getAttestationObject(), registrationData.getCollectedClientData(), registrationData.getClientExtensions(), registrationData.getTransports());
                    return inMemoryPasskeysCredentialRecordService.createCredentialRecord(credentialRecord).subscribeAsCompletionStage();
                })
                .thenApply(dummy -> Response.status(Response.Status.OK).build());

        return Uni.createFrom().completionStage(response);
    }


}
