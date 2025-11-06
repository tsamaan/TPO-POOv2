package auth;

import models.Usuario;

public class GoogleAuthAdapter implements AuthProvider {

    @Override
    public Usuario authenticate(Object credentials) {
        // dummy implementation: return a google-authenticated user
        return new Usuario(2, "GoogleUser", "google@example.com");
    }
}
