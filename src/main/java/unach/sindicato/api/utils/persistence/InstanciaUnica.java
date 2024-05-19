package unach.sindicato.api.utils.persistence;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bson.types.ObjectId;

/**
 * Representa una propiedad de una colección que es única.
 */

@Data
public class InstanciaUnica {
    @NotNull(message = "Se debe proporcionar un id") ObjectId id;
}
