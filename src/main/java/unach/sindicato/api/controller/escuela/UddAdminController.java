package unach.sindicato.api.controller.escuela;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unach.sindicato.api.controller.AuthController;
import unach.sindicato.api.controller.PersistenceController;
import unach.sindicato.api.persistence.escuela.UddAdmin;
import unach.sindicato.api.service.escuela.UddAdminService;
import unach.sindicato.api.utils.Correo;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.response.UddResponse;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class UddAdminController implements PersistenceController<UddAdmin>, AuthController<UddAdmin> {
    final UddAdminService service;

    @Override
    public @NonNull UddAdminService service() {
        return service;
    }

    @PreAuthorize("hasAuthority('administrador')")
    @PostMapping("/where/correo/is")
    public UddResponse findByCorreo(@RequestBody @Validated(InitInfo.class) Correo correo) {
        AuthController.logger.post(MaestroController.class);

        return UddResponse.collection()
                .status(HttpStatus.OK)
                .collection(service.findByCorreo(correo))
                .message("Admin encontrado correctamente")
                .build();
    }
}
