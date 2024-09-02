package com.webauthn4j.quarkus.passkeys.lib.challenge;

import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.util.Base64UrlUtil;
import io.quarkus.vertx.http.runtime.security.PersistentLoginManager;
import io.vertx.ext.web.RoutingContext;

public class HttpCookieChallengeRepository implements ChallengeRepository{

    private final PersistentLoginManager loginManager;
    private final String cookieName;

    public HttpCookieChallengeRepository(PersistentLoginManager loginManager, String cookieName) {
        this.loginManager = loginManager;
        this.cookieName = cookieName;
    }

    @Override
    public Challenge generateChallenge() {
        return new DefaultChallenge();
    }

    @Override
    public void saveChallenge(Challenge challenge, RoutingContext context) {
        String challengeString = null;
        if (challenge != null) {
            challengeString = Base64UrlUtil.encodeToString(challenge.getValue());
        }
        loginManager.save(challengeString, context, cookieName, null, context.request().isSSL());
    }

    @Override
    public Challenge loadChallenge(RoutingContext context) {
        PersistentLoginManager.RestoreResult result = loginManager.restore(context, cookieName);
        if(result == null){
            return null;
        }
        else{
            return new DefaultChallenge(result.getPrincipal());
        }
    }
}
