package unach.sindicato.api.persistence.escuela;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.groups.IdInfo;
import unach.sindicato.api.utils.groups.NotId;
import unach.sindicato.api.utils.persistence.Nombrable;
import unach.sindicato.api.utils.persistence.Unico;

/**
 * @author Kevin Alejandro Francisco González
 * Representa una locación o edificio usado para impartir y promocionar carreras educativas
 * relacionadas con un área en específico.
 */

@Data
@Document(collection = "escuela")
public class Facultad implements Unico, Nombrable {
    @Null(message = "No se debe proporcionar un id",
            groups = NotId.class)
    @NotNull(message = "Se debe proporcionar un id",
            groups = IdInfo.class)
    private ObjectId id;
    @NotBlank(message = "Se debe proporcionar un nombre",
            groups = InitInfo.class)
    @Pattern(message = "Nombre invalido",
            regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$",
            groups = InitInfo.class)
    protected String nombre;
    @NotBlank(message = "Se debe proporcionar el campus",
            groups = InitInfo.class)
    @Pattern(message = "Campus invalido",
            regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$",
            groups = InitInfo.class)
    protected String campus;
}
