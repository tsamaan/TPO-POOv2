package auth;

import models.Usuario;

public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public Usuario register(String username, String email, String password) {
        return authService.registerUser(username, email, password);
    }

    public Usuario login(String email, String password) {
        return authService.loginUser(email, password);
    }

    public Usuario loginWithProvider(String providerName, Object credentials) {
        return authService.loginWithProvider(providerName, credentials);
    }
}
