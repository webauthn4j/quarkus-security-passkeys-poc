package com.webauthn4j.quarkus.passkeys.lib;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.time.Duration;
import java.util.Optional;

@ConfigMapping(prefix = "quarkus.passkeys")
//@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface PasskeysRunTimeConfig {


//    /**
//     * Length of the challenges exchanged between the application and the browser.
//     * Challenges must be at least 32 bytes.
//     */
//    @ConfigDocDefault("64")
//    OptionalInt challengeLength();


    /**
     * The login page
     */
    @WithDefault("/login.html")
    String loginPage();

    /**
     * The inactivity (idle) timeout
     *
     * When inactivity timeout is reached, cookie is not renewed and a new login is enforced.
     */
    @WithDefault("PT30M")
    Duration sessionTimeout();

    /**
     * How old a cookie can get before it will be replaced with a new cookie with an updated timeout, also
     * referred to as "renewal-timeout".
     *
     * Note that smaller values will result in slightly more server load (as new encrypted cookies will be
     * generated more often); however, larger values affect the inactivity timeout because the timeout is set
     * when a cookie is generated.
     *
     * For example if this is set to 10 minutes, and the inactivity timeout is 30m, if a user's last request
     * is when the cookie is 9m old then the actual timeout will happen 21m after the last request because the timeout
     * is only refreshed when a new cookie is generated.
     *
     * That is, no timeout is tracked on the server side; the timestamp is encoded and encrypted in the cookie
     * itself, and it is decrypted and parsed with each request.
     */
    @WithDefault("PT1M")
    Duration newCookieInterval();

    /**
     * The cookie that is used to store the persistent session
     */
    @WithDefault("quarkus-credential")
    String cookieName();

    /**
     * The cookie that is used to store the challenge data during login/registration
     */
    @WithDefault("_quarkus_webauthn_challenge")
    public String challengeCookieName();

    /**
     * The cookie that is used to store the username data during login/registration
     */
    @WithDefault("_quarkus_webauthn_username")
    public String challengeUsernameCookieName();

    @WithDefault("strict")
    CookieSameSite cookieSameSite();

    enum CookieSameSite {
        STRICT,
        LAX,
        NONE
    }

    /**
     * The cookie path for the session cookies.
     */
    @WithDefault("/")
    Optional<String> cookiePath();
}
