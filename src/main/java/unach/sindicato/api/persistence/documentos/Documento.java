package unach.sindicato.api.persistence.documentos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.validation.annotation.Validated;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.persistence.data.Formatos;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.groups.IdInfo;
import unach.sindicato.api.utils.groups.NotId;
import unach.sindicato.api.utils.persistence.Unico;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Kevin Alejandro Francisco González.
 * Representa un documento abstracto que pertenece a un maestro.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "content")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Pdf.class, name = "pdf"),
        @JsonSubTypes.Type(value = Documento.class, name = "none")})

@Data
@Document(collection = "documentos")
public class Documento implements Unico {
    @Null(message = "No se debe proporcionar un id",
            groups = NotId.class)
    @NotNull(message = "Se debe proporcionar un id",
            groups = IdInfo.class)
    private ObjectId id;
    @NotNull(message = "Se debe proporcionar un formato",
            groups = InitInfo.class)
    protected Formatos formato;
    @Null(message = "No se debe proporcionar una propiedad reportes",
            groups = InitInfo.class)
    @Null(message = "No se debe proporcionar un reporte durante la creación",
            groups = InitInfo.class)
    @Valid
    protected List<Reporte> reportes = new ArrayList<>();
    private TipoContenido content;

    @Field("content")
    @JsonProperty("content")
    public TipoContenido getContent() {
        return TipoContenido.none;
    }

    public Optional<Reporte> getUltimoReporte() {
        if (reportes.isEmpty()) return Optional.empty();
        return Optional.of(reportes.get(reportes.size() - 1));
    }

    public void add(@NonNull Reporte reporte) {
        reportes.add(reporte);
    }

    @AllArgsConstructor
    public enum Estatus {
        ACEPTADO("#51D97F"),
        INCORRECTO("#F02137"),
        NO_ACEPTADO("#F02137"),
        REQUIERE_ACTUALIZAR("#F47F04"),
        REQUIERE_VALIDAR("#5F6368");

        public final String hexColor;
    }

    public enum TipoContenido {
        pdf,
        none,
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reporte {
        @NotNull(message = "Se debe proporcionar un motivo",
                groups = InitInfo.class)
        private Estatus motivo;
        @NotEmpty(message = "Se debe proporcionar una descripcion",
                groups = InitInfo.class)
        private String descripcion;
        @Null(message = "No se debe proporcionar una propiedad fecha",
                groups = InitInfo.class)
        private final LocalDateTime fecha = LocalDateTime.now();

        public Reporte(@NonNull Estatus estatus) {
            this.motivo = estatus;
            this.descripcion = estatus.name();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Documento documento)
            return formato.equals(documento.formato);
        return false;
    }

    @Override
    public int hashCode() {
        return formato.hashCode();
    }

    @Validated(IdInfo.class)
    public record Entrada(@Valid Documento documento,
                          @Valid Maestro maestro) {
    }
}
