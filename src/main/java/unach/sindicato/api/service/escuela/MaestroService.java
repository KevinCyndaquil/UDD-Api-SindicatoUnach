package unach.sindicato.api.service.escuela;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
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
import unach.sindicato.api.utils.Roles;
import unach.sindicato.api.utils.errors.DocumentoNoActualizadoException;
import unach.sindicato.api.utils.errors.PdfSinBytesException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaestroService implements PersistenceService<Maestro>, AuthService<Maestro> {
    final MaestroRepository repository;
    final JwtService jwtService;

    final MongoTemplate mongoTemplate;
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

    @Override
    public @NonNull Roles expectedRol() {
        return Roles.maestro;
    }

    public Set<Maestro> findByFacultad(@NonNull Facultad facultad) {
        return repository.findByFacultadId(facultad.getId());
    }

    public Set<Maestro> findByCampus(@NonNull String campus) {
        return repository.findByFacultadCampus(campus);
    }

    public Maestro findByCorreo(@NonNull Correo correo) {
        return repository.findByCorreo_institucional(correo.getDireccion(), clazz().getName());
    }

    public Set<Maestro> findByEstatus(@NonNull Maestro.Estatus estatus) {
        return repository.findByEstatus(estatus);
    }

    public List<Maestro> findByEstatusDocumento(@NonNull Documento.Estatus estatus) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_class").is(Maestro.class.getName())),
                LookupOperation.newLookup()
                        .from("documentos")
                        .localField("documentos.$id")
                        .foreignField("_id")
                        .as("documentos"),
                Aggregation.project().andExclude("oa.documentos.bytes"),
                Aggregation.match(Criteria.where("documentos.reporte.motivo").is(estatus))
        );

        AggregationResults<Maestro> result = mongoTemplate.aggregate(
                aggregation,
                "escuela",
                Maestro.class
        );

        System.out.println(result.getMappedResults());
        return result.getMappedResults();
    }

    @Transactional
    public boolean addDocumentos(@NonNull Maestro maestro) {
        Set<Pdf> documentos = maestro.getDocumentos()
                .stream()
                .map(d -> {
                    if (d.getClass().equals(Pdf.class)) return (Pdf) d;
                    throw new PdfSinBytesException(d);
                })
                .collect(Collectors.toSet());

        Maestro maestroSaved = findById(maestro);
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
                    pdfSaved.setEncrypted(false);
                });

        maestroSaved.getDocumentos().forEach(pdf -> {
            Documento documentoSaved = documentoService.saveOrUpdate((Pdf) pdf);
            pdf.setId(documentoSaved.getId());
        });

        if (!update(maestroSaved))
            throw new DocumentoNoActualizadoException(maestroSaved, getClass());
        return true;
    }

    @Component
    public static class EventListener extends AbstractMongoEventListener<Maestro> {
        @Override
        public void onBeforeSave(@NonNull BeforeSaveEvent<Maestro> event) {
            if (event.getDocument() == null) return;

            Maestro maestro = event.getSource();
            maestro.setEstatus(maestro.validar());

            event.getDocument().put("estatus", maestro.getEstatus());
            event.getDocument().put("rol", maestro.getRol());
        }
    }
}
