package unach.sindicato.api.controller;

import jakarta.validation.Valid;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import unach.sindicato.api.service.persistence.PersistenceService;
import unach.sindicato.api.utils.UddLogger;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.groups.NotId;
import unach.sindicato.api.utils.persistence.InstanciaUnica;
import unach.sindicato.api.utils.persistence.Unico;
import unach.sindicato.api.utils.response.UddResponse;

import java.util.Set;

/**
 * Controlador de persistencia generico para la API de UDD.
 * @param <C> el tipo elemental de la colección del controlador.
 */

@Validated
@EnableMethodSecurity
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT})
public interface PersistenceController <C extends Unico> {
    UddLogger logger = new UddLogger(PersistenceController.class);

    @NonNull PersistenceService<C> service();

    @PostMapping
    @PreAuthorize("hasAuthority('administrador')")
    default UddResponse save(@RequestBody@Validated({InitInfo.class, NotId.class}) C c) {
        logger.post(getClass());

        return UddResponse.collection()
                .status(HttpStatus.CREATED)
                .message("%s fue persistido correctamente"
                        .formatted(service().clazz().getSimpleName()))
                .collection(service().save(c))
                .build();
    }

    @PostMapping("/all")
    @PreAuthorize("hasAuthority('administrador')")
    default UddResponse save(@RequestBody@Validated({InitInfo.class, NotId.class}) Set<C> c) {
        logger.post(getClass());

        return UddResponse.collection()
                .status(HttpStatus.CREATED)
                .message("%ss fueron persistidos correctamente"
                        .formatted(service().clazz().getSimpleName()))
                .collection(service().save(c))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('administrador')")
    default UddResponse findAll() {
        logger.get(getClass());

        return UddResponse.collection()
                .status(HttpStatus.OK)
                .message("Listado de %s encontrados correctamente"
                        .formatted(service().clazz().getSimpleName()))
                .collection(service().findAll())
                .build();
    }

    @PostMapping("/where/id/is")
    @PreAuthorize("hasAuthority('administrador')")
    default UddResponse findById(@RequestBody@Valid InstanciaUnica instancia) {
        logger.post(getClass());

        return UddResponse.collection()
                .status(HttpStatus.OK)
                .message("%s encontrado correctamente"
                        .formatted(service().clazz().getSimpleName()))
                .collection(service().findById(instancia))
                .build();
    }

    @PutMapping
    @PreAuthorize("hasAuthority('administrador')")
    default UddResponse update(@RequestBody@Validated({InitInfo.class, NotId.class}) C c) {
        logger.put(getClass());

        String message = service().update(c) ?
                "%s fue actualizado correctamente".formatted(service().clazz().getSimpleName()) :
                "%s no fue modificado".formatted(service().clazz().getSimpleName());

        return UddResponse.info()
                .status(HttpStatus.ACCEPTED)
                .message(message)
                .build();
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('administrador')")
    default UddResponse delete(@NonNull@RequestParam("id") ObjectId id) {
        logger.delete(getClass());

        String message = service().delete(id) ?
                "%s=%s fue eliminado correctamente".formatted(service().clazz().getSimpleName(), id) :
                "%s no fue modificado".formatted(service().clazz().getSimpleName());

        return UddResponse.info()
                .status(HttpStatus.ACCEPTED)
                .message(message)
                .build();
    }
}
