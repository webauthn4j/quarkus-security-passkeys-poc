# quarkus-security-passkeys-poc

This project is a POC to demonstrate that webauthn4j-core-async can be used in combination with Quarkus Security. 
It is not intended to provide an actual production level `IdentityProvider` for Quarkus Security.

## Running the application in dev mode

You can run this POC in dev mode using:

```shell script
./mvnw compile quarkus:dev
```

## Prerequisites
b
- Chrome 129, which is in Beta status as of Aug. 2024, is required since this POC uses PublicKeyCredential JSON serialization to simplify the implementation.
