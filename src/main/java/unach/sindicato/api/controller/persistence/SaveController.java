package unach.sindicato.api.controller.persistence;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import unach.sindicato.api.service.persistence.SaveService;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.groups.NotId;
import unach.sindicato.api.utils.persistence.Unico;
import unach.sindicato.api.utils.response.UddResponse;

import java.util.Set;

@Validated
@EnableMethodSecurity
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT})
public interface SaveController <C extends Unico> {

    @NonNull SaveService<C> service();

    @PostMapping
    //@PreAuthorize("hasAuthority('administrador')")
    default UddResponse save(@RequestBody@Validated({InitInfo.class, NotId.class}) C c) {
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
        return UddResponse.collection()
                .status(HttpStatus.CREATED)
                .message("%ss fueron persistidos correctamente"
                        .formatted(service().clazz().getSimpleName()))
                .collection(service().save(c))
                .build();
    }
}
