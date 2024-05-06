package unach.sindicato.api.persistence.sujetos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.utils.UddUser;
import unach.sindicato.api.utils.Roles;
import unach.sindicato.api.utils.Telefono;
import unach.sindicato.api.utils.groups.PostInfo;

import java.util.Set;

/**
 * Persona encargada de impartir clases y que para UDD, tiene documentos e información necesaria
 * para su administración.
 */

@JsonTypeName("maestro")

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "sujetos")
public class Maestro extends UddUser {
    @Valid
    Set<Documento> documentos;
    @Valid
    @NotNull(groups = PostInfo.class)
    Telefono telefono;
    @Valid
    Facultad facultad;
    @NotNull(groups = PostInfo.class)
    Estatus estatus = Estatus.SIN_VALIDACION;

    @Override
    public @NonNull Roles getRol() {
        return Roles.MAESTRO;
    }

    @Override
    public String getUsername() {
        return getCorreo().getDireccion();
    }

    public enum Estatus {
        VALIDADO,
        SIN_VALIDACION,
        DOCUMENTOS_INCOMPLETOS
    }
}