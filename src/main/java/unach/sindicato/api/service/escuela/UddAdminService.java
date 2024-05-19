package unach.sindicato.api.service.escuela;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.persistence.escuela.UddAdmin;
import unach.sindicato.api.repository.UddAdminRepository;
import unach.sindicato.api.service.auth.AuthService;
import unach.sindicato.api.service.auth.JwtService;
import unach.sindicato.api.service.documentos.DocumentoService;
import unach.sindicato.api.service.persistence.PersistenceService;
import unach.sindicato.api.utils.Correo;
import unach.sindicato.api.utils.Roles;
import unach.sindicato.api.utils.error.DocumentoNoActualizadoException;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UddAdminService implements PersistenceService<UddAdmin>, AuthService<UddAdmin> {
    final UddAdminRepository repository;
    final JwtService jwtService;

    final MaestroService maestroService;
    final DocumentoService documentoService;

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

    @Transactional
    public boolean addReportes(@NonNull Maestro maestro) {
        Set<Documento> documentos = maestro.getDocumentos();
        Maestro maestroSaved = maestroService.findById(maestro);

        documentos.forEach(doc -> {
            Documento documentoSaved = maestroSaved.getDocumentos()
                    .stream()
                    .reduce((ac, ds) -> ds.equals(doc) ? ds : ac)
                    .orElseThrow();
            documentoSaved.setReporte(doc.getReporte());
        });

        boolean result = maestroSaved.getDocumentos()
                .stream()
                .map(documentoService::update)
                .reduce(true, Boolean::logicalAnd);

        if (!result)
            throw new DocumentoNoActualizadoException(maestroSaved.getDocumentos(), getClass());

        return maestroService.update(maestroSaved);
    }
}
