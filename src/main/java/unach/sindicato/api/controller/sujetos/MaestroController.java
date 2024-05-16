package unach.sindicato.api.controller.sujetos;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unach.sindicato.api.controller.AuthController;
import unach.sindicato.api.controller.PersistenceController;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.service.escuela.MaestroService;
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
}
