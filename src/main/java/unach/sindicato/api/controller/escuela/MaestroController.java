package unach.sindicato.api.controller.escuela;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unach.sindicato.api.controller.AuthController;
import unach.sindicato.api.controller.PersistenceController;
import unach.sindicato.api.persistence.escuela.Facultad;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.service.escuela.MaestroService;
import unach.sindicato.api.utils.Correo;
import unach.sindicato.api.utils.groups.DocumentInfo;
import unach.sindicato.api.utils.groups.IdInfo;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.response.UddResponse;

@RestController
@RequestMapping("/maestros")
@RequiredArgsConstructor
public class MaestroController implements PersistenceController<Maestro>, AuthController<Maestro> {
    final MaestroService service;

    @Override
    public @NonNull MaestroService service() {
        return service;
    }

    @Override
    @Transactional
    public UddResponse save(Maestro maestro) {
        return register(maestro);
    }

    @PreAuthorize("hasAuthority('administrador')")
    @PostMapping("/where/facultad/is")
    public UddResponse findByFacultad(@RequestBody@Validated(IdInfo.class) Facultad facultad) {
        AuthController.logger.post(MaestroController.class);

        return UddResponse.collection()
                .status(HttpStatus.OK)
                .collection(service.findByFacultad(facultad))
                .message("Maestros encontrados correctamente")
                .build();
    }

    @PreAuthorize("hasAuthority('administrador')")
    @PostMapping("/where/correo/is")
    public UddResponse findByCorreo(@RequestBody@Validated(InitInfo.class) Correo correo) {
        AuthController.logger.post(MaestroController.class);

        return UddResponse.collection()
                .status(HttpStatus.OK)
                .collection(service.findByCorreo(correo))
                .message("Maestro encontrado correctamente")
                .build();
    }

    @PostMapping("/add/documentos")
    public UddResponse addDocumentos(
            @RequestBody@Validated({DocumentInfo.class, IdInfo.class}) Maestro maestro) {
        AuthController.logger.post(MaestroController.class);

        return UddResponse.result()
                .status(HttpStatus.OK)
                .result(service.addDocumentos(maestro))
                .message("Documentos añadidos correctamente")
                .build();
    }
}
