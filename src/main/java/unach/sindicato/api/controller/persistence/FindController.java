package unach.sindicato.api.controller.persistence;

import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import unach.sindicato.api.service.persistence.FindService;
import unach.sindicato.api.utils.persistence.InstanciaUnica;
import unach.sindicato.api.utils.persistence.Unico;
import unach.sindicato.api.utils.response.UddResponse;

@Validated
@EnableMethodSecurity
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT})
public interface FindController <C extends Unico> {
    @NonNull FindService<C> service();

    @GetMapping
    @PreAuthorize("hasAuthority('administrador')")
    default UddResponse findAll() {
        return UddResponse.collection()
                .status(HttpStatus.OK)
                .message("Listado de %s encontrados correctamente"
                        .formatted(service().clazz().getSimpleName()))
                .collection(service().findAll())
                .build();
    }

    @PostMapping("/where/id-is")
    @PreAuthorize("hasAuthority('administrador')")
    default UddResponse findById(@RequestBody@Valid InstanciaUnica instancia) {
        return UddResponse.collection()
                .status(HttpStatus.OK)
                .message("%s encontrado correctamente"
                        .formatted(service().clazz().getSimpleName()))
                .collection(service().findById(instancia))
                .build();
    }
}
