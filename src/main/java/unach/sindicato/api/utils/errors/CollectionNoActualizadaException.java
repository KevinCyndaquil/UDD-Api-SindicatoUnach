package unach.sindicato.api.utils.errors;

import lombok.NonNull;

public class CollectionNoActualizadaException extends RuntimeException {

    public CollectionNoActualizadaException(Object collection, @NonNull Class<?> source) {
        super("Ocurrió un error durante una actualización, se intento modificar a %s en %s"
                .formatted(collection, source.getName()));
    }
}
