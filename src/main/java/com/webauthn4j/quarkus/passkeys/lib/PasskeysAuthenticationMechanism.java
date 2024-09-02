package com.webauthn4j.quarkus.passkeys.lib;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TrustedAuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpSecurityUtils;
import io.quarkus.vertx.http.runtime.security.PersistentLoginManager;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import org.jboss.logging.Logger;

import java.util.function.Consumer;

public class PasskeysAuthenticationMechanism implements HttpAuthenticationMechanism {

    private static final Logger log = Logger.getLogger(PasskeysAuthenticationMechanism.class);

    private final PersistentLoginManager loginManager;
    private String loginPage;

    public PasskeysAuthenticationMechanism(PersistentLoginManager loginManager, String loginPage){
        this.loginManager = loginManager;
        this.loginPage = loginPage;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        PersistentLoginManager.RestoreResult result = loginManager.restore(context);
        if (result != null) {
            context.put(HttpAuthenticationMechanism.class.getName(), this);
            Uni<SecurityIdentity> ret = identityProviderManager
                    .authenticate(HttpSecurityUtils
                            .setRoutingContextAttribute(new TrustedAuthenticationRequest(result.getPrincipal()), context));
            return ret.onItem().invoke(new Consumer<SecurityIdentity>() {
                @Override
                public void accept(SecurityIdentity securityIdentity) {
                    loginManager.save(securityIdentity, context, result, context.request().isSSL());
                }
            });
        }
        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        log.debugf("Serving login form %s for %s", loginPage, context);
        return getRedirect(context, loginPage);
    }

    static Uni<ChallengeData> getRedirect(final RoutingContext exchange, final String location) {
        String loc = exchange.request().scheme() + "://" + exchange.request().authority() + location;
        return Uni.createFrom().item(new ChallengeData(302, "Location", loc));
    }

    public PersistentLoginManager getLoginManager() {
        return loginManager;
    }
}
