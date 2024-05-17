package unach.sindicato.api.utils.errors;

import lombok.NonNull;
import unach.sindicato.api.utils.persistence.Credential;

public class CredencialInvalidaException extends RuntimeException {

    public CredencialInvalidaException(@NonNull Credential credential) {
        super("Credenciales %s=%s son incorrectas"
                .formatted(credential.getCorreo(), credential.getPassword()));
    }
}
