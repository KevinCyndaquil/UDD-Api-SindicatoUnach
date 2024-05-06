package unach.sindicato.api.persistence.sujetos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import unach.sindicato.api.utils.groups.PostInfo;
import unach.sindicato.api.utils.persistence.Nombrable;
import unach.sindicato.api.utils.persistence.Unico;

@Data
@Document(collection = "sujetos")
public class Facultad implements Unico, Nombrable {
    @Null(groups = {PostInfo.class})
    ObjectId id;
    @NotBlank(groups = {PostInfo.class})
    @Pattern(regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$", groups = {PostInfo.class})
    String nombre;
    @NotBlank(groups = {PostInfo.class})
    @Pattern(regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$", groups = {PostInfo.class})
    String campus;
}
