package unach.sindicato.api.controller.sujetos;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unach.sindicato.api.controller.AuthController;
import unach.sindicato.api.controller.PersistenceController;
import unach.sindicato.api.persistence.sujetos.Maestro;
import unach.sindicato.api.service.sujetos.MaestroService;

@RestController
@RequestMapping("/maestros")
@RequiredArgsConstructor
public class MaestroController implements PersistenceController<Maestro>, AuthController<Maestro> {
    final MaestroService service;

    @Override
    public @NonNull MaestroService service() {
        return service;
    }
}
