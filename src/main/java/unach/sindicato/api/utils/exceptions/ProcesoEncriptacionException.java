package unach.sindicato.api.utils.exceptions;

import lombok.NonNull;
import unach.sindicato.api.persistence.escuela.UsuarioUDD;

public class ProcesoEncriptacionException extends RuntimeException {

    public ProcesoEncriptacionException(@NonNull UsuarioUDD user) {
        super("Ocurrio un error durante la encriptación de %s con rol %s"
                .formatted(user.getNombre(), user.getRol()));
    }
}
