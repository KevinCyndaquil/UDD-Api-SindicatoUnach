package unach.sindicato.api.utils.error;

import lombok.NonNull;

public class DocumentoNoActualizadoException extends RuntimeException {

    public DocumentoNoActualizadoException(Object collection, @NonNull Class<?> source) {
        super("Ocurrió un error durante una actualización, se intento modificar a %s en %s"
                .formatted(collection, source.getName()));
    }
}
