package unach.sindicato.api.controller.escuela;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unach.sindicato.api.controller.persistence.PersistenceController;
import unach.sindicato.api.persistence.escuela.Facultad;
import unach.sindicato.api.service.escuela.FacultadService;
import unach.sindicato.api.service.persistence.PersistenceService;

@RestController
@RequestMapping("facultades")
@RequiredArgsConstructor
public class FacultadController implements PersistenceController<Facultad> {
    final FacultadService service;

    @Override
    public @NonNull PersistenceService<Facultad> service() {
        return service;
    }
}
