package auth;

import models.Usuario;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private Map<String, AuthProvider> providers = new HashMap<>();

    public AuthService() {
        // Register default providers
        providers.put("local", new LocalAuthAdapter());
        providers.put("google", new GoogleAuthAdapter());
    }

    public Usuario registerUser(String username, String email, String password) {
        // dummy registration logic
        return new Usuario(100, username, email);
    }

    public Usuario loginUser(String email, String password) {
        // dummy login logic with local auth
        return providers.get("local").authenticate(email);
    }

    public Usuario loginWithProvider(String providerName, Object credentials) {
        AuthProvider provider = providers.get(providerName);
        if (provider != null) {
            return provider.authenticate(credentials);
        }
        return null;
    }
}
