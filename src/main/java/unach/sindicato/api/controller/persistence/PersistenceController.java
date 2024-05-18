package unach.sindicato.api.controller.persistence;

import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import unach.sindicato.api.service.persistence.PersistenceService;
import unach.sindicato.api.utils.persistence.Unico;
import unach.sindicato.api.utils.response.UddResponse;

/**
 * Controlador de persistencia generico para la API de UDD.
 * @param <C> el tipo elemental de la colección del controlador.
 */

@Validated
@EnableMethodSecurity
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT})
public interface PersistenceController <C extends Unico> extends SaveController<C>, FindController<C>, UpdateController<C> {
    @NonNull PersistenceService<C> service();

    @DeleteMapping
    @PreAuthorize("hasAuthority('administrador')")
    default UddResponse delete(@NonNull@RequestParam("id") ObjectId id) {
        return UddResponse.info()
                .status(HttpStatus.OK)
                .message(service().delete(id) ?
                        "%s=%s fue eliminado correctamente"
                                .formatted(service().clazz().getSimpleName(), id) :
                        "%s no fue modificado"
                                .formatted(service().clazz().getSimpleName()))
                .build();
    }
}
