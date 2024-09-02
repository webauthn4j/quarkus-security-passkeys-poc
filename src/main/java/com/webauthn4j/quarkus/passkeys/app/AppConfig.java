package com.webauthn4j.quarkus.passkeys.app;

import com.webauthn4j.async.WebAuthnAuthenticationAsyncManager;
import com.webauthn4j.async.WebAuthnRegistrationAsyncManager;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.quarkus.passkeys.lib.*;
import com.webauthn4j.quarkus.passkeys.lib.challenge.ChallengeRepository;
import com.webauthn4j.quarkus.passkeys.lib.challenge.HttpCookieChallengeRepository;
import io.quarkus.runtime.Startup;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.vertx.http.runtime.HttpConfiguration;
import io.quarkus.vertx.http.runtime.security.PersistentLoginManager;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;

import java.security.SecureRandom;
import java.util.Base64;

@ApplicationScoped
public class AppConfig {

    private static final Logger log = Logger.getLogger(AppConfig.class);

    //the temp encryption key, persistent across dev mode restarts
    static volatile String encryptionKey;

    @ApplicationScoped
    @Produces
    public WebAuthnRegistrationAsyncManager webAuthnRegistrationAsyncManager(){
        return WebAuthnRegistrationAsyncManager.createNonStrictWebAuthnRegistrationAsyncManager();
    }

    @ApplicationScoped
    @Produces
    public WebAuthnAuthenticationAsyncManager webAuthnAuthenticationAsyncManager(){
        return new WebAuthnAuthenticationAsyncManager();
    }

    @ApplicationScoped
    @Produces
    public PersistentLoginManager persistentLoginManager(HttpConfiguration httpConfiguration, PasskeysRunTimeConfig config){
        String key;
        if (!httpConfiguration.encryptionKey.isPresent()) {
            if (encryptionKey != null) {
                //persist across dev mode restarts
                key = encryptionKey;
            } else {
                byte[] data = new byte[32];
                new SecureRandom().nextBytes(data);
                key = encryptionKey = Base64.getEncoder().encodeToString(data);
                log.warn(
                        "Encryption key was not specified (using `quarkus.http.auth.session.encryption-key` configuration) for persistent WebAuthn auth, using temporary key "
                                + key);
            }
        } else {
            key = httpConfiguration.encryptionKey.get();
        }

        return new PersistentLoginManager(key, config.cookieName(),
                config.sessionTimeout().toMillis(),
                config.newCookieInterval().toMillis(), false, config.cookieSameSite().name(),
                config.cookiePath().orElse(null));
    }

    @ApplicationScoped
    @Produces
    public PasskeysAuthenticationMechanism passkeysAuthenticationMechanism(PersistentLoginManager loginManager, PasskeysRunTimeConfig config){
        return new PasskeysAuthenticationMechanism(loginManager, config.loginPage());
    }

    @ApplicationScoped
    @Produces
    public PasskeysIdentityProvider passkeysIdentityProvider(WebAuthnAuthenticationAsyncManager webAuthnAuthenticationAsyncManager){
        return new PasskeysIdentityProvider(webAuthnAuthenticationAsyncManager);
    }

    @ApplicationScoped
    @Produces
    public PasskeysTrustedIdentityProvider passkeysTrustedIdentityProvider(){
        return new PasskeysTrustedIdentityProvider();
    }

    @ApplicationScoped
    @Produces
    public InMemoryPasskeysCredentialRecordService inMemoryPasskeysCredentialRecordService(){
        return new InMemoryPasskeysCredentialRecordService();
    }

    @ApplicationScoped
    @Produces
    public ChallengeRepository challengeRepository(PersistentLoginManager loginManager, PasskeysRunTimeConfig config){
        return new HttpCookieChallengeRepository(loginManager, config.challengeCookieName());
    }

    @Startup
    @Singleton
    @Produces
    public PasskeysAuthenticationEndpoint passkeysAuthenticationEndpoint(Router router, IdentityProviderManager identityProviderManager, PasskeysCredentialRecordService passkeysCredentialRecordService, PasskeysAuthenticationMechanism passkeysAuthenticationMechanism, ChallengeRepository challengeRepository){
        BodyHandler bodyHandler = BodyHandler.create();
        PasskeysAuthenticationEndpoint passkeysAuthenticationEndpoint = new PasskeysAuthenticationEndpoint(identityProviderManager, passkeysCredentialRecordService, passkeysAuthenticationMechanism, challengeRepository, new ObjectConverter());
        router.post("/passkeys/authenticate").handler(bodyHandler).handler(passkeysAuthenticationEndpoint::handleRequest);
        return passkeysAuthenticationEndpoint;
    }

    @Startup
    @Singleton
    @Produces
    public PasskeysAttestationOptionsEndpoint passkeysAttestationOptionsEndpoint(Router router, ChallengeRepository challengeRepository){
        BodyHandler bodyHandler = BodyHandler.create();
        PasskeysAttestationOptionsEndpoint passkeysAttestationOptionsEndpoint = new PasskeysAttestationOptionsEndpoint(challengeRepository);
        router.get("/passkeys/attestationOptions").handler(bodyHandler).handler(passkeysAttestationOptionsEndpoint::handleRequest);
        return passkeysAttestationOptionsEndpoint;
    }

    @Startup
    @Singleton
    @Produces
    public PasskeysAssertionOptionsEndpoint passkeysAssertionOptionsEndpoint(Router router, ChallengeRepository challengeRepository){
        BodyHandler bodyHandler = BodyHandler.create();
        PasskeysAssertionOptionsEndpoint passkeysAssertionOptionsEndpoint = new PasskeysAssertionOptionsEndpoint(challengeRepository);
        router.get("/passkeys/assertionOptions").handler(bodyHandler).handler(passkeysAssertionOptionsEndpoint::handleRequest);
        return passkeysAssertionOptionsEndpoint;
    }

}
