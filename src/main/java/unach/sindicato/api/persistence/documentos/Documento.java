package unach.sindicato.api.persistence.documentos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.validation.annotation.Validated;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.utils.Formatos;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.groups.IdInfo;
import unach.sindicato.api.utils.groups.NotId;
import unach.sindicato.api.utils.persistence.Unico;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "content")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Pdf.class, name = "pdf"),
        @JsonSubTypes.Type(value = Documento.class, name = "none")})

@Data
@Document(collection = "documentos")
public class Documento implements Unico {
    @Null(message = "No se debe proporcionar una propiedad id",
            groups = NotId.class)
    @NotNull(message = "Se debe proporcionar un identificador",
            groups = IdInfo.class)
    ObjectId id;
    @NotNull(message = "Se requiere un formato",
            groups = InitInfo.class)
    Formatos formato;
    @Null(message = "No se debe proporcionar una propiedad reporte",
            groups = InitInfo.class)
    @Valid Reporte reporte;
    Contents content;

    @Field("content")
    @JsonProperty("content")
    public Contents getContent() {
        return Contents.none;
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

    public enum Contents {
        pdf,
        none
    }

    public Pdf asPdf() {
        if (content == Contents.pdf) return (Pdf) this;
        throw new IllegalArgumentException(
                "Documento que se esperaba ser pdf, no contiene la propiedad content como pdf");
    }

    @Data
    public static class Reporte {
        @NotNull(message = "Se requiere un motivo",
                groups = InitInfo.class)
        Estatus motivo;
        @NotEmpty(message = "Se requiere una descripcion",
                groups = InitInfo.class)
        String descripcion;

        public static @NonNull Reporte motivo(Estatus estatus) {
            Reporte reporte = new Reporte();
            reporte.setMotivo(estatus);
            return reporte;
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
                          @Valid Maestro maestro) {}
}
