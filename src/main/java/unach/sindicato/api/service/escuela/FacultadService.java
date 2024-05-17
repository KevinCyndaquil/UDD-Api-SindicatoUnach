package unach.sindicato.api.service.escuela;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import unach.sindicato.api.persistence.escuela.Facultad;
import unach.sindicato.api.repository.FacultadRepository;
import unach.sindicato.api.repository.UddRepository;
import unach.sindicato.api.service.persistence.PersistenceService;

@Service
@RequiredArgsConstructor
public class FacultadService implements PersistenceService<Facultad> {
    final FacultadRepository repository;

    @Override
    public @NonNull UddRepository<Facultad> repository() {
        return repository;
    }

    @Override
    public @NonNull Class<Facultad> clazz() {
        return Facultad.class;
    }
}
