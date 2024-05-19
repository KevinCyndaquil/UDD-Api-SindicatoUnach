package unach.sindicato.api.utils.error;

import lombok.NonNull;

import java.util.Map;

public class BusquedaSinResultadoException extends RuntimeException {

    public BusquedaSinResultadoException(
            @NonNull Class<?> _class,
            @NonNull String propiedad,
            @NonNull Object valor) {
        this(_class, Map.of(propiedad, valor));
    }

    public BusquedaSinResultadoException(
            @NonNull Class<?> _class,
            @NonNull Map<String, Object> propiedades) {
        super("No se hallaron resultados de %s usando el filtro ( %s)"
                .formatted(_class.getSimpleName(), propiedades.entrySet()
                        .stream()
                        .map(e -> "%s=%s ".formatted(e.getKey(), e.getValue()))
                        .reduce("", String::concat)));
    }
}
