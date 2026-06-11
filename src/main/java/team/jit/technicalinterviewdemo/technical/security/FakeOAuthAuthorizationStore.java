package team.jit.technicalinterviewdemo.technical.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Profile("fake-oauth & !prod")
class FakeOAuthAuthorizationStore {

    private final ConcurrentMap<String, AuthorizedUser> authorizationCodes = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, AuthorizedUser> accessTokens = new ConcurrentHashMap<>();

    String issueGrantCode(AuthorizedUser user) {
        String authorizationCode = UUID.randomUUID().toString();
        authorizationCodes.put(authorizationCode, user);
        return authorizationCode;
    }

    Optional<AuthorizedUser> consumeGrantCode(String authorizationCode) {
        return Optional.ofNullable(authorizationCodes.remove(authorizationCode));
    }

    String createAccessToken(AuthorizedUser user) {
        String accessToken = UUID.randomUUID().toString();
        accessTokens.put(accessToken, user);
        return accessToken;
    }

    Optional<AuthorizedUser> findAccessToken(String accessToken) {
        return Optional.ofNullable(accessTokens.get(accessToken));
    }

    record AuthorizedUser(String login, String displayName, String email, Set<String> scope) {}
}
