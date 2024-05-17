package unach.sindicato.api.utils.errors;

import lombok.NonNull;
import unach.sindicato.api.utils.persistence.Credential;

public class CredencialInvalidaException extends RuntimeException {

    public CredencialInvalidaException(@NonNull Credential credential) {
        super("Credenciales para %s=%s no se encontraron en nuestra base de datos"
                .formatted(credential.getCorreo(), credential.getPassword()));
    }
}
