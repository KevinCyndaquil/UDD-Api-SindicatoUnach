package unach.sindicato.api.persistence.documentos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
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

    @Field("content")
    @JsonProperty("content")
    public Contents getContent() {
        return Contents.none;
    }

    public enum Estatus {
        ACEPTADO,
        INCORRECTO,
        NO_ACEPTADO,
        REQUIERE_ACTUALIZAR,
        REQUIERE_VALIDAR
    }

    public enum Contents {
        pdf,
        none
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reporte {
        @NotNull(message = "Se requiere un motivo",
                groups = InitInfo.class)
        Estatus motivo;
        @NotEmpty(message = "Se requiere una descripcion",
                groups = InitInfo.class)
        String descripcion;
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
}
