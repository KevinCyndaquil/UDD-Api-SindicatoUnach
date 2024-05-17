package unach.sindicato.api.utils.errors;

import lombok.NonNull;
import unach.sindicato.api.utils.Roles;
import unach.sindicato.api.utils.persistence.Credential;

public class CredencialInvalidaException extends RuntimeException {

    public CredencialInvalidaException(@NonNull Credential credential, Roles rolExpected) {
        super("Credenciales para %s %s=%s no se encontraron en nuestra base de datos"
                .formatted(rolExpected, credential.getCorreo(), credential.getPassword()));
    }
}
