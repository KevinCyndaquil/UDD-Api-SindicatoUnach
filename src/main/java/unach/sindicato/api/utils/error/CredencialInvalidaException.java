package unach.sindicato.api.utils.error;

import lombok.NonNull;
import unach.sindicato.api.utils.Roles;
import unach.sindicato.api.utils.persistence.Credencial;

public class CredencialInvalidaException extends RuntimeException {

    public CredencialInvalidaException(@NonNull Credencial credencial, Roles rolExpected) {
        super("Credenciales para %s %s=%s no se encontraron en nuestra base de datos"
                .formatted(rolExpected, credencial.getCorreo(), credencial.getPassword()));
    }

    public CredencialInvalidaException(@NonNull Credencial credencial, Roles rolExpected, String cause) {
        super("Credenciales para %s %s=%s no se encontraron en nuestra base de datos, porque %s"
                .formatted(rolExpected, credencial.getCorreo(), credencial.getPassword(), cause));
    }
}
