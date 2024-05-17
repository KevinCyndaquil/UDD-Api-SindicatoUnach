package unach.sindicato.api.utils.response;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import unach.sindicato.api.utils.UddMapper;
import unach.sindicato.api.utils.errors.Errors;

/**
 * Objeto que se utiliza como respuesta del servidor ante cualquier situación necesaria.
 * Se utiliza en lugar de ResponseEntity y se deben utilizar sus métodos estaticos.
 */

@Getter
public class UddResponse extends ResponseEntity<UddResponse.Properties> {

    @Builder(builderMethodName = "info", builderClassName = "ResponseInfoBuilder")
    protected UddResponse(HttpStatus status, String message) {
        super(Properties.builder()
                .message(message)
                .build(), status);
    }

    @Builder(builderMethodName = "error", builderClassName = "ResponseErrorBuilder")
    protected UddResponse(HttpStatus status, @NonNull Errors error, String message) {
        super(Properties.builder()
                .error(error)
                .message(message)
                .build(), status);
    }

    @Builder(builderMethodName = "collection", builderClassName = "ResponseCollectionBuilder")
    protected UddResponse(HttpStatus status, String message, @NonNull Object collection) {
        super(Properties.builder()
                .message(message)
                .json(collection)
                .build(), status);
    }

    @Builder(builderMethodName = "result", builderClassName = "ResponseResultBuilder")
    protected UddResponse(HttpStatus status, String message, boolean result) {
        super(Properties.builder()
                .message(message)
                .result(result)
                .build(), status);
    }

    @Getter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Properties {
        Errors error;
        @NonNull String message;
        Object json;
        Boolean result;

        public <T> T jsonAs(TypeReference<T> _class) {
            if (json == null) return null;
            return new UddMapper().convertValue(json, _class);
        }

        public <T> T jsonAs(Class<T> _class) {
            if (json == null) return null;
            return new UddMapper().convertValue(json, _class);
        }
    }
}