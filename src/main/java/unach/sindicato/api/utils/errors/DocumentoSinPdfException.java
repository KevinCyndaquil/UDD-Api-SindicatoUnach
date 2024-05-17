package unach.sindicato.api.utils.errors;

import lombok.NonNull;
import unach.sindicato.api.persistence.documentos.Documento;

public class DocumentoSinPdfException extends RuntimeException {
    public DocumentoSinPdfException(@NonNull Documento documento) {
        super("Se necesita que el documento %s contenga un pdf apropiado"
                .formatted(documento.getFormato()));
    }
}
