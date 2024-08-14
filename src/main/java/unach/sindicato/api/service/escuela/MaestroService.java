package unach.sindicato.api.service.escuela;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
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
import unach.sindicato.api.persistence.data.Correo;
import unach.sindicato.api.persistence.data.RolesUsuario;
import unach.sindicato.api.utils.exceptions.BusquedaSinResultadoException;
import unach.sindicato.api.utils.exceptions.DocumentoNoActualizadoException;

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
    public @NonNull RolesUsuario expectedRol() {
        return RolesUsuario.maestro;
    }

    @Override
    public List<Maestro> findAll() {
        return repository.findAllExcludingBytes(Maestro.class.getName());
    }

    public Maestro findByIdExcludingPdf(@NonNull ObjectId id) throws BusquedaSinResultadoException {
        var maestro = AuthService.super.findById(id);
        maestro.getDocumentos()
                .stream()
                .filter(d -> d.getContent().equals(Documento.TipoContenido.pdf))
                .map(Pdf.class::cast)
                .forEach(d -> d.setBytes(null));
        return maestro;
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
                Aggregation.match(Criteria.where("documentos.reportes.motivo").is(estatus))
        );

        return mongoTemplate.aggregate(
                aggregation,
                "escuela",
                Maestro.class
        ).getMappedResults();
    }

    @Transactional
    public boolean addDocumentos(@NonNull Maestro maestro) {
        System.out.println(maestro);
        Set<Pdf> documentos = maestro.getDocumentos()
                .stream()
                .map(Pdf.class::cast)
                .peek(d -> System.out.println(d.getContent()))
                .collect(Collectors.toSet());

        Maestro maestroSaved = findById(maestro);
        maestroSaved.setPassword(null);

        documentos
                .stream()
                .filter(pdf -> pdf.getBytes() != null)
                .forEach(pdf -> {
            pdf.add(new Documento.Reporte(Documento.Estatus.REQUIERE_VALIDAR));

            if (maestroSaved.getDocumentos().add(pdf)) return;

            Pdf pdfSaved = maestroSaved.getDocumentos()
                    .stream()
                    .reduce((ac, docSaved) -> docSaved.equals(pdf) ? docSaved : ac)
                    .map(Pdf.class::cast)
                    .orElseThrow();

            pdfSaved.setReportes(pdf.getReportes());
            pdfSaved.setBytes(pdf.getBytes());
            pdfSaved.setEncrypted(false);
        });
        maestroSaved.getDocumentos().forEach(doc -> {
            ObjectId docId = documentoService.saveOrUpdate((Pdf) doc).getId();
            doc.setId(docId);
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
