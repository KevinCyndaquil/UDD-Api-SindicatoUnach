package unach.sindicato.api.persistence.escuela;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.persistence.data.RolesUsuario;
import unach.sindicato.api.persistence.data.Telefono;
import unach.sindicato.api.utils.groups.DocumentInfo;
import unach.sindicato.api.utils.groups.InitInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kevin Alejandro Francisco González.
 * Persona encargada de impartir clases y que para UDD, tiene documentos e información necesaria
 * para su administración.
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "escuela")
public class Maestro extends UsuarioUDD {
    @DBRef
    @NotEmpty(message = "Se debe proporcionar al menos un documento",
            groups = DocumentInfo.class)
    private Set<Documento> documentos = new HashSet<>();
    @NotNull(message = "Se debe proporcionar un télefono",
            groups = InitInfo.class)
    @Valid
    private Telefono telefono;
    @Valid
    private Facultad facultad;
    @Null(message = "El estatus es calculado, favor de no proporcionarlo durante la creación",
            groups = InitInfo.class)
    private Estatus estatus;

    @Override
    public @NonNull RolesUsuario getRol() {
        return RolesUsuario.maestro;
    }

    public Estatus validar() {
        for (var doc : documentos) {
            var reporte = doc.getUltimoReporte().orElseThrow();

            if (!reporte.getMotivo().equals(Documento.Estatus.ACEPTADO))
                if (reporte.getMotivo().equals(Documento.Estatus.REQUIERE_VALIDAR))
                    return Estatus.SIN_VALIDAR;
                else
                    return Estatus.ERROR_DOCUMENTACION;
        }
        return Estatus.VALIDADO;
    }

    @Override
    public String getUsername() {
        return getCorreo_institucional().getDireccion();
    }

    public enum Estatus {
        VALIDADO,
        SIN_VALIDAR,
        ERROR_DOCUMENTACION,
        SIN_DOCUMENTAR
    }
}
