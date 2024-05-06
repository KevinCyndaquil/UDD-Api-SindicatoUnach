package unach.sindicato.api.utils.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import unach.sindicato.api.utils.UddMapper;
import unach.sindicato.api.utils.errors.Errors;

/**
 * Objeto que se utiliza como respuesta del servidor ante cualquier situación necesaria.
 * Se utiliza en lugar de ResponseEntity y se deben utilizar sus métodos estaticos.
 */

@Getter
public class UddResponse extends ResponseEntity<ResponseProperties> {
    ResponseProperties properties;

    @Builder(builderMethodName = "info", builderClassName = "ResponseInfoBuilder")
    protected UddResponse(HttpStatus status, String message) {
        super(ResponseProperties.builder()
                .status(status)
                .message(message)
                .build(), status);
    }

    @Builder(builderMethodName = "error", builderClassName = "ResponseErrorBuilder")
    protected UddResponse(HttpStatus status, @NonNull Errors error, String message) {
        super(ResponseProperties.builder()
                .status(status)
                .error(error)
                .message(message)
                .build(), status);
    }

    @Builder(builderMethodName = "collection", builderClassName = "ResponseCollectionBuilder")
    protected UddResponse(HttpStatus status, String message, @NonNull Object collection) {
        super(ResponseProperties.builder()
                .status(status)
                .message(message)
                .json(UddMapper.writeValue(collection))
                .build(), status);
    }
}