package unach.sindicato.api.utils.errors;

import lombok.NonNull;
import unach.sindicato.api.persistence.documentos.Pdf;

public class ErrorEncriptacionException extends RuntimeException {
    public ErrorEncriptacionException(@NonNull Pdf pdf) {
        super("Ocurrió un error durante la encriptación del documento %s"
                .formatted(pdf.getFormato()));
    }
}
