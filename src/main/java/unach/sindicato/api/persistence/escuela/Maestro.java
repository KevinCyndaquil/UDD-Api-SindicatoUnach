package unach.sindicato.api.persistence.escuela;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.utils.UddUser;
import unach.sindicato.api.utils.Roles;
import unach.sindicato.api.utils.Telefono;
import unach.sindicato.api.utils.groups.DocumentInfo;
import unach.sindicato.api.utils.groups.InitInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * Persona encargada de impartir clases y que para UDD, tiene documentos e información necesaria
 * para su administración.
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "escuela")
public class Maestro extends UddUser {
    @DBRef(lazy = true)
    @NotEmpty(message = "Se requiere al menos un documento",
            groups = DocumentInfo.class)
    Set<Documento> documentos = new HashSet<>();
    @NotNull(message = "Se requiere un télefono",
            groups = InitInfo.class)
    @Valid Telefono telefono;
    @Valid Facultad facultad;
    @Null(message = "El estatus es calculado, favor de no proporcionarlo",
            groups = InitInfo.class)
    Estatus estatus;

    @Override
    public @NonNull Roles getRol() {
        return Roles.maestro;
    }

    public Estatus validar() {
        if (documentos.isEmpty()) return Estatus.SIN_DOCUMENTAR;

        var reporteSinValidar = documentos.stream()
                .map(Documento::getReporte)
                .filter(r -> r.getMotivo().equals(Documento.Estatus.REQUIERE_VALIDAR))
                .findFirst();
        if (reporteSinValidar.isPresent()) return Estatus.SIN_VALIDAR;

        var reportesValidos = documentos.stream()
                .map(Documento::getReporte)
                .map(r -> r.getMotivo().equals(Documento.Estatus.ACEPTADO))
                .reduce(true, Boolean::logicalAnd);
        if (reportesValidos) return Estatus.VALIDADO;

        return Estatus.ERROR_DOCUMENTACION;
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
