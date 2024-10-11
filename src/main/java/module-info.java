module com.webauthn4j.quarkus.passkeys.poc {

    requires com.webauthn4j.core.async;

    requires com.fasterxml.jackson.core;
    requires io.quarkus.security.api;
    requires io.smallrye.mutiny;
    requires io.vertx.core;
    requires io.vertx.web;
    requires jakarta.annotation;
    requires jakarta.cdi;
    requires jakarta.inject;
    requires jakarta.transaction;
    requires jakarta.ws.rs;
    requires org.jboss.logging;
    requires quarkus.core;
    requires quarkus.security;
    requires quarkus.vertx.http;
    requires smallrye.config.core;
}
