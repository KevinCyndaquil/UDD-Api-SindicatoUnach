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

@Data
@Document(collection = "escuela")
public class Facultad implements Unico, Nombrable {
    @Null(message = "No se debe proporcionar una propiedad id",
            groups = NotId.class)
    @NotNull(message = "Se requiere un identificador",
            groups = IdInfo.class)
    ObjectId id;
    @NotBlank(message = "Se requiere un nombre",
            groups = InitInfo.class)
    @Pattern(message = "Nombre invalido",
            regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$",
            groups = InitInfo.class)
    String nombre;
    @NotBlank(message = "Se requiere el campus",
            groups = InitInfo.class)
    @Pattern(message = "Campus invalido",
            regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$",
            groups = InitInfo.class)
    String campus;
}
