package unach.sindicato.api.controller.escuela;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import unach.sindicato.api.controller.AuthController;
import unach.sindicato.api.controller.persistence.PersistenceController;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.persistence.escuela.Facultad;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.service.escuela.MaestroService;
import unach.sindicato.api.utils.Correo;
import unach.sindicato.api.utils.groups.DocumentInfo;
import unach.sindicato.api.utils.groups.IdInfo;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.response.UddResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/estatus")
    public UddResponse getEstatus() {
        List<Map<String, String>> estatus = Arrays.stream(Maestro.Estatus.values())
                .map(e -> Map.of("name", e.name()))
                .toList();

        return UddResponse.collection()
                .status(HttpStatus.OK)
                .collection(estatus)
                .message("Estatus de maestro encontrados correctamente")
                .build();
    }

    @PreAuthorize("hasAuthority('administrador')")
    @GetMapping("/where/estatus-documento-is")
    public UddResponse findByEstatusDocumento(@RequestParam("estatus") Documento.Estatus estatus) {
        return UddResponse.collection()
                .status(HttpStatus.OK)
                .collection(service.findByEstatusDocumento(estatus))
                .message("Documentos encontrados correctamente")
                .build();
    }

    @PreAuthorize("hasAuthority('administrador')")
    @GetMapping("/where/estatus-is")
    public UddResponse findByEstatus(@RequestParam("estatus") Maestro.Estatus estatus) {
        return UddResponse.collection()
                .status(HttpStatus.OK)
                .collection(service.findByEstatus(estatus))
                .message("Maestros encontrados correctamente")
                .build();
    }

    @PreAuthorize("hasAuthority('administrador')")
    @PostMapping("/where/facultad-is")
    public UddResponse findByFacultad(@RequestBody@Validated(IdInfo.class) Facultad facultad) {
        AuthController.logger.post(MaestroController.class);

        return UddResponse.collection()
                .status(HttpStatus.OK)
                .collection(service.findByFacultad(facultad))
                .message("Maestros encontrados correctamente")
                .build();
    }

    @PreAuthorize("hasAuthority('administrador')")
    @GetMapping("/where/campus-is")
    public UddResponse findByCampus(@RequestParam("campus") String campus) {
        AuthController.logger.post(MaestroController.class);

        return UddResponse.collection()
                .status(HttpStatus.OK)
                .collection(service.findByCampus(campus))
                .message("Maestros encontrados correctamente")
                .build();
    }

    @PreAuthorize("hasAuthority('administrador')")
    @PostMapping("/where/correo-is")
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
