package com.webauthn4j.quarkus.passkeys.lib.challenge;

import com.webauthn4j.data.client.challenge.Challenge;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

public interface ChallengeRepository {
    /**
     * Generates a {@link Challenge}
     *
     * @return the {@link Challenge} that was generated. Cannot be null.
     */
    Challenge generateChallenge();

    /**
     * Saves the {@link Challenge} using the {@link RoutingContext} and
     * {@link RoutingContext}. If the {@link Challenge} is null, it is the same as
     * deleting it.
     *
     * @param challenge the {@link Challenge} to save or null to delete
     * @param context   the {@link RoutingContext} to use
     */
    void saveChallenge(Challenge challenge, RoutingContext context);

    /**
     * Loads the expected {@link Challenge} from the {@link RoutingContext}
     *
     * @param context the {@link HttpServerRequest} to use
     * @return the {@link Challenge} or null if none exists
     */
    Challenge loadChallenge(RoutingContext context);

    /**
     * Loads or generates {@link Challenge} from the {@link RoutingContext}
     *
     * @param context the {@link RoutingContext} to use
     * @return the {@link Challenge} or null if none exists
     */
    default Challenge loadOrGenerateChallenge(RoutingContext context) {
        Challenge challenge = this.loadChallenge(context);
        if (challenge == null) {
            challenge = this.generateChallenge();
            this.saveChallenge(challenge, context);
        }
        return challenge;
    }
}
