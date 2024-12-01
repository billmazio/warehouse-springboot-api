package gr.clothesmanager.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklistService {
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public void revokeToken(String token) {
        blacklist.add(token);
    }

    public boolean isTokenRevoked(String token) {
        return blacklist.contains(token);
    }
}
