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
import unach.sindicato.api.persistence.data.Correo;
import unach.sindicato.api.persistence.data.RolesUsuario;
import unach.sindicato.api.utils.exceptions.DocumentoNoActualizadoException;

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
    public @NonNull RolesUsuario expectedRol() {
        return RolesUsuario.administrador;
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

    @Override
    public boolean update(@NonNull UddAdmin uddAdmin) {
        return AuthService.super.update(uddAdmin);
    }

    @Transactional
    public boolean addReportes(@NonNull Maestro maestroUpload) {
        Set<Documento> documentos = maestroUpload.getDocumentos();
        Maestro maestroSaved = maestroService.findById(maestroUpload);
        System.out.println("Contraseña del request " + maestroUpload.getPassword());

        documentos.forEach(doc -> {
            Documento documentoSaved = maestroSaved.getDocumentos()
                    .stream()
                    .reduce((ac, ds) -> ds.equals(doc) ? ds : ac)
                    .orElseThrow();
            var reporte = doc.getUltimoReporte();
            if (reporte.isEmpty())
                throw new IllegalArgumentException("Documento " + doc.getContent().name() + " no contiene ningún reporte");
            documentoSaved.getReportes().add(reporte.get());
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
