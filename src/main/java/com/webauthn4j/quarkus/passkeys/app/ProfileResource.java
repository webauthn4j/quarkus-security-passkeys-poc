package com.webauthn4j.quarkus.passkeys.app;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

@Path("/profile")
public class ProfileResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String me(@Context SecurityContext securityContext) {
        Principal user = securityContext.getUserPrincipal();
        return user != null ? user.getName() : "<not logged in>";
    }
}
