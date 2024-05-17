package unach.sindicato.api.service.escuela;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import unach.sindicato.api.persistence.escuela.UddAdmin;
import unach.sindicato.api.repository.UddAdminRepository;
import unach.sindicato.api.service.auth.AuthService;
import unach.sindicato.api.service.auth.JwtService;
import unach.sindicato.api.service.persistence.PersistenceService;
import unach.sindicato.api.utils.Correo;
import unach.sindicato.api.utils.Roles;

@Service
@RequiredArgsConstructor
public class UddAdminService implements PersistenceService<UddAdmin>, AuthService<UddAdmin> {
    final UddAdminRepository repository;
    final JwtService jwtService;

    @Override
    public @NonNull JwtService jwtService() {
        return jwtService;
    }

    @Override
    public @NonNull Roles expectedRol() {
        return Roles.administrador;
    }

    @Override
    public @NonNull UddAdminRepository repository() {
        return repository;
    }

    @Override
    public @NonNull Class<UddAdmin> clazz() {
        return UddAdmin.class;
    }

    public UddAdmin findByCorreo(@NonNull Correo correo) {
        return repository.findByCorreo_institucional(correo.getDireccion(), clazz().getName());
    }
}
