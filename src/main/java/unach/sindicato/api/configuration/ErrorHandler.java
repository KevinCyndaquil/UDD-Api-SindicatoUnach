package unach.sindicato.api.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import unach.sindicato.api.utils.UddLogger;
import unach.sindicato.api.utils.errors.Errors;
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
}
