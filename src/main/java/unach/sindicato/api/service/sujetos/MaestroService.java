package unach.sindicato.api.service.sujetos;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import unach.sindicato.api.persistence.sujetos.Maestro;
import unach.sindicato.api.repository.MaestroRepository;
import unach.sindicato.api.service.auth.AuthService;
import unach.sindicato.api.service.persistence.PersistenceService;
import unach.sindicato.api.service.auth.JwtService;

@Service
@RequiredArgsConstructor
public class MaestroService implements PersistenceService<Maestro>, AuthService<Maestro> {
    final MaestroRepository repository;
    final JwtService jwtService;

    @Override
    public @NonNull MaestroRepository repository() {
        return repository;
    }

    @Override
    public @NonNull Class<Maestro> clazz() {
        return Maestro.class;
    }

    @Override
    public @NonNull JwtService jwtService() {
        return jwtService;
    }
}
