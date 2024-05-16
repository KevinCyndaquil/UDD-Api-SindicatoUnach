package unach.sindicato.api.persistence.documentos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import unach.sindicato.api.utils.Formatos;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.groups.IdInfo;
import unach.sindicato.api.utils.groups.NotId;
import unach.sindicato.api.utils.persistence.Unico;

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
    byte[] contenido;
    @Null(message = "No se debe proporcionar una propiedad reporte",
            groups = InitInfo.class)
    @Valid Reporte reporte;

    public enum Estatus {
        ACEPTADO,
        NO_ACEPTADO,
        REQUIERE_ACTUALIZAR,
        REQUIERE_VALIDAR
    }

    @Data
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
