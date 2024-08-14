package unach.sindicato.api.utils.exceptions;

import lombok.NonNull;
import unach.sindicato.api.utils.persistence.Nombrable;

/**
 * Una colección contiene un objeto nombrable con un nombre repetido.
 */
public class NombrableRepetidoException extends RuntimeException {

    public NombrableRepetidoException(@NonNull Nombrable nombrable) {
        super("Colección llamado %s y nombrado como único ya se encuentra persistido"
                .formatted(nombrable.getNombre()));
    }
}
