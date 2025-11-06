package auth;

import models.Usuario;

public interface AuthProvider {
    Usuario authenticate(Object credentials);
}
