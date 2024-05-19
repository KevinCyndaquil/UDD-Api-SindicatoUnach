package unach.sindicato.api.configuration;

import com.mongodb.MongoCommandException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import unach.sindicato.api.utils.UddLogger;
import unach.sindicato.api.utils.error.*;
import unach.sindicato.api.utils.response.UddResponse;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ErrorHandler {
    final UddLogger logger = new UddLogger(ErrorHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public UddResponse handleAccessDenied(AccessDeniedException e) {
        logger.error(e);

        return UddResponse.error()
                .status(HttpStatus.UNAUTHORIZED)
                .error(Errors.WITHOUT_AUTHORIZATION_ERROR)
                .message("No tienes permiso para acceder a este recurso")
                .build();
    }

    @ExceptionHandler({
            BusquedaSinResultadoException.class,
            NombrableRepetidoException.class
    })
    public UddResponse handleBusquedaSinResultado(RuntimeException e) {
        logger.error(e);
        return UddResponse.error()
                .status(HttpStatus.BAD_REQUEST)
                .error(Errors.QUERY_WITHOUT_RESPONSE_ERROR)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(CredencialInvalidaException.class)
    public UddResponse handleCredencialInvalida(CredencialInvalidaException e) {
        logger.error(e);
        return UddResponse.error()
                .status(HttpStatus.UNAUTHORIZED)
                .error(Errors.QUERY_WITHOUT_RESPONSE_ERROR)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public UddResponse handleIllegalArgument(IllegalArgumentException e) {
        logger.error(e);
        return UddResponse.error()
                .status(HttpStatus.BAD_REQUEST)
                .error(Errors.ILLEGAL_PARAMETER_ERROR)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public UddResponse handleIllegalArgument(MethodArgumentNotValidException e) {
        logger.error(e);

        String message = e.getFieldError() == null ?
                "Hay un problema con la propiedad %s"
                        .formatted(e.getParameter().getParameterName()) :
                e.getFieldError().getDefaultMessage();

        return UddResponse.error()
                .status(HttpStatus.BAD_REQUEST)
                .error(Errors.ILLEGAL_PARAMETER_ERROR)
                .message(message)
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public UddResponse handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        logger.error(e);

        return UddResponse.error()
                .status(HttpStatus.BAD_REQUEST)
                .error(Errors.UNINTELIGIBLE_REQUEST_ERROR)
                .message("Cuerpo de la solicitud no pudo ser entendida por el servidor")
                .build();
    }

    @ExceptionHandler({
            MongoCommandException.class
    })
    public UddResponse handleInternalError(Exception e) {
        logger.error(e);
        return UddResponse.error()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .error(Errors.SERVER_ERROR)
                .message("Ocurrió un error inesperado en el servidor, favor de contactar al administrador")
                .build();
    }

    @ExceptionHandler({
            DocumentoNoActualizadoException.class,
            ErrorEncriptacionException.class,
            ProcesoEncriptacionException.class
    })
    public UddResponse handleDocumentoNoActualizado(Exception e) {
        logger.error(e);
        return UddResponse.error()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .error(Errors.ROLLBACK_ACTION_ERROR)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(PdfSinBytesException.class)
    public UddResponse handlePdfSinBytes(PdfSinBytesException e) {
        logger.error(e);
        return UddResponse.error()
                .status(HttpStatus.BAD_REQUEST)
                .error(Errors.ROLLBACK_ACTION_ERROR)
                .message(e.getMessage())
                .build();
    }
}
