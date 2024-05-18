package unach.sindicato.api.controller.persistence;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import unach.sindicato.api.service.persistence.UpdateService;
import unach.sindicato.api.utils.groups.IdInfo;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.persistence.Unico;
import unach.sindicato.api.utils.response.UddResponse;

@Validated
@EnableMethodSecurity
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT})
public interface UpdateController <C extends Unico> {
    @NonNull UpdateService<C> service();

    @PutMapping
    @PreAuthorize("hasAuthority('administrador')")
    default UddResponse update(@RequestBody @Validated({InitInfo.class, IdInfo.class}) C c) {
        return UddResponse.info()
                .status(HttpStatus.OK)
                .message(service().update(c) ?
                        "%s fue actualizado correctamente"
                                .formatted(service().clazz().getSimpleName()) :
                        "%s no fue modificado"
                                .formatted(service().clazz().getSimpleName()))
                .build();
    }
}
