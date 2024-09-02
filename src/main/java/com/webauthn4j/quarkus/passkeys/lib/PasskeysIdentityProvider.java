package com.webauthn4j.quarkus.passkeys.lib;

import com.webauthn4j.async.WebAuthnAuthenticationAsyncManager;
import com.webauthn4j.data.AuthenticationParameters;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;

public class PasskeysIdentityProvider implements IdentityProvider<PasskeysAuthenticationRequest> {

    private final WebAuthnAuthenticationAsyncManager webAuthnAuthenticationAsyncManager;

    public PasskeysIdentityProvider(WebAuthnAuthenticationAsyncManager webAuthnAuthenticationAsyncManager) {
        this.webAuthnAuthenticationAsyncManager = webAuthnAuthenticationAsyncManager;
    }

    @Override
    public Class<PasskeysAuthenticationRequest> getRequestType() {
        return PasskeysAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(PasskeysAuthenticationRequest request, AuthenticationRequestContext context) {
        return Uni.createFrom().emitter(emitter -> {
            String authenticationResponseJSON = request.getAuthenticationResponseJSON();
            AuthenticationParameters authenticationParameters = new AuthenticationParameters(request.getServerProperty(), request.getCredentialRecord(), request.getAllowCredentials(), request.isUserVerificationRequired(), request.isUserPresenceRequired());
            webAuthnAuthenticationAsyncManager.verify(authenticationResponseJSON, authenticationParameters).thenAccept(authenticationData -> {
                QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder();
                String username = request.getUsername();
                builder.setPrincipal(new QuarkusPrincipal(username));
                emitter.complete(builder.build());
            }).exceptionally(e ->{
                emitter.fail(e);
                return null;
            });
        });
    }
}
