package unach.sindicato.api.utils.persistence;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Representa una propiedad de una colección que es única.
 * @param <T> el tipo elemental de la propiedad.
 */

@Data
public class InstanciaUnica <T> {
    @NotNull T id;
}
