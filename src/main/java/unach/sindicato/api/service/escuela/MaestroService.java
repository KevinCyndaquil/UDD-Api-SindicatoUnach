package unach.sindicato.api.service.escuela;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.persistence.documentos.Pdf;
import unach.sindicato.api.persistence.escuela.Facultad;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.repository.MaestroRepository;
import unach.sindicato.api.service.auth.AuthService;
import unach.sindicato.api.service.documentos.DocumentoService;
import unach.sindicato.api.service.persistence.PersistenceService;
import unach.sindicato.api.service.auth.JwtService;
import unach.sindicato.api.utils.Correo;
import unach.sindicato.api.utils.errors.CollectionNoActualizadaException;
import unach.sindicato.api.utils.errors.DocumentoSinPdfException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaestroService implements PersistenceService<Maestro>, AuthService<Maestro> {
    final MaestroRepository repository;
    final JwtService jwtService;

    final DocumentoService documentoService;

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

    public Set<Maestro> findByFacultad(@NonNull Facultad facultad) {
        return repository.findByFacultadId(facultad.getId());
    }

    public Maestro findByCorreo(@NonNull Correo correo) {
        return repository.findByCorreo_institucional(correo.getDireccion());
    }

    @Transactional
    public boolean addDocumentos(@NonNull Maestro maestro) {
        Set<Pdf> documentos = maestro.getDocumentos()
                .stream()
                .map(d -> {
                    if (d.getClass().equals(Pdf.class)) return (Pdf) d;
                    throw new DocumentoSinPdfException(d);
                })
                .collect(Collectors.toSet());

        System.out.println(documentos);
        Maestro maestroSaved = findById(maestro);
        System.out.println(maestroSaved.getDocumentos());
        Documento.Reporte reporteSinValidar = Documento.Reporte.builder()
                .motivo(Documento.Estatus.REQUIERE_VALIDAR)
                .build();

        documentos.stream()
                .peek(pdf -> pdf.setReporte(reporteSinValidar))
                .forEach(pdf -> {
                    if (maestroSaved.getDocumentos().add(pdf)) return;

                    Pdf pdfSaved = maestroSaved.getDocumentos()
                            .stream()
                            .reduce((ac, ds) -> ds.equals(pdf) ? ds : ac)
                            .map(Pdf.class::cast)
                            .orElseThrow();

                    pdfSaved.setReporte(reporteSinValidar);
                    pdfSaved.setBytes(pdf.getBytes());
                });

        maestroSaved.getDocumentos().forEach(pdf -> {
            Documento documentoSaved = documentoService.saveOrUpdate((Pdf) pdf);
            pdf.setId(documentoSaved.getId());
        });

        if (!update(maestroSaved))
            throw new CollectionNoActualizadaException(maestroSaved, getClass());
        return true;
    }
}
