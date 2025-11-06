package auth;

import models.Usuario;

public class LocalAuthAdapter implements AuthProvider {

    @Override
    public Usuario authenticate(Object credentials) {
        // dummy implementation: credentials expected as String username
        if (credentials instanceof String) {
            return new Usuario(1, (String)credentials, "local@example.com");
        }
        return null;
    }
}
