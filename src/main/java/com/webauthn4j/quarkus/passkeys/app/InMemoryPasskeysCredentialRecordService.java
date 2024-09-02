package com.webauthn4j.quarkus.passkeys.app;

import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.quarkus.passkeys.lib.CredentialIdNotFoundException;
import com.webauthn4j.quarkus.passkeys.lib.PasskeysCredentialRecord;
import com.webauthn4j.quarkus.passkeys.lib.PasskeysCredentialRecordService;
import com.webauthn4j.quarkus.passkeys.lib.PrincipalNotFoundException;
import com.webauthn4j.util.Base64UrlUtil;
import io.smallrye.mutiny.Uni;

import java.util.*;

public class InMemoryPasskeysCredentialRecordService implements PasskeysCredentialRecordService {

    private final Map<String, Map<String, PasskeysCredentialRecord>> map = new HashMap<>();

    public Uni<Void> createCredentialRecord(PasskeysCredentialRecord passkeysCredentialRecord) {
        String username = passkeysCredentialRecord.getUsername();
        if(!map.containsKey(passkeysCredentialRecord.getUsername())){
            map.put(username, new HashMap<>());
        }
        map.get(username).put(Base64UrlUtil.encodeToString(passkeysCredentialRecord.getAttestedCredentialData().getCredentialId()), passkeysCredentialRecord);
        return Uni.createFrom().voidItem();
    }

    public void deleteCredentialRecord(byte[] credentialId) {
        for (Map.Entry<String, Map<String, PasskeysCredentialRecord>> entry : map.entrySet()){
            CredentialRecord credentialRecord = entry.getValue().get(Base64UrlUtil.encodeToString(credentialId));
            if(credentialRecord != null){
                entry.getValue().remove(Base64UrlUtil.encodeToString(credentialId));
                return;
            }
        }
        throw new CredentialIdNotFoundException("credentialId not found.");
    }

    public boolean credentialRecordExists(byte[] credentialId) {
        return map.values().stream().anyMatch(innerMap -> innerMap.get(Base64UrlUtil.encodeToString(credentialId)) != null);
    }

    @Override
    public Uni<Void> updateCounter(byte[] credentialId, long counter) throws CredentialIdNotFoundException {
        return this.loadCredentialRecordByCredentialId(credentialId).onItem().transformToUni( credentialRecord -> {
            credentialRecord.setCounter(counter);
            return Uni.createFrom().voidItem();
        });
    }

    @Override
    public Uni<PasskeysCredentialRecord> loadCredentialRecordByCredentialId(byte[] credentialId) throws CredentialIdNotFoundException {
        return Uni.createFrom().item(map.values().stream().
                map(innerMap ->
                        innerMap.get(Base64UrlUtil.encodeToString(credentialId))
                ).filter(Objects::nonNull)
                .findFirst().orElseThrow(()-> new CredentialIdNotFoundException("credentialId not found.")));
    }

    @Override
    public Uni<List<PasskeysCredentialRecord>> loadCredentialRecordsByUserPrincipal(String principal) {
        Map<String, PasskeysCredentialRecord> innerMap = map.get(principal);
        if(innerMap == null || innerMap.isEmpty()){
            throw new PrincipalNotFoundException("principal not found.");
        }
        return Uni.createFrom().item(Collections.unmodifiableList(new ArrayList<>(innerMap.values())));
    }
}
