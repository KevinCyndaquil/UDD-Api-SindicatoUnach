package unach.sindicato.api.persistence.documentos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import unach.sindicato.api.utils.Formatos;
import unach.sindicato.api.utils.groups.PostInfo;
import unach.sindicato.api.utils.persistence.Unico;

@Data
@Document(collection = "documentos")
public class Documento implements Unico {
    @Null(groups = PostInfo.class)
    ObjectId id;
    @NotNull(groups = PostInfo.class)
    Formatos formato;
    byte[] contenido;
    Reporte reporte;

    public enum Estatus {
        ACEPTADO,
        NO_ACEPTADO,
        REQUIERE_ACTUALIZAR,
        REQUIERE_VALIDAR
    }

    @Data
    public static class Reporte {
        @NotNull(groups = PostInfo.class)
        Estatus motivo;
        @NotEmpty(groups = PostInfo.class)
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
