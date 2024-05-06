package unach.sindicato.api.utils.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import unach.sindicato.api.utils.errors.Errors;

/**
 * Objeto que contiene las propiedades que una UddResponse puede llegar a utilizar para la
 * respuesta de un servicio.
 */

@Getter
@Builder
public class ResponseProperties {
    @NonNull HttpStatus status;
    Errors error;
    @NonNull String message;
    @JsonRawValue String json;
}
