package unach.sindicato.api.utils.errors;

import lombok.NonNull;
import unach.sindicato.api.utils.UddUser;

public class ProcesoEncriptacionException extends RuntimeException {

    public ProcesoEncriptacionException(@NonNull UddUser user) {
        super("Ocurrio un error durante la encriptación de %s con rol %s"
                .formatted(user.getNombre(), user.getRol()));
    }
}
