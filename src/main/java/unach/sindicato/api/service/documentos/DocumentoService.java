package unach.sindicato.api.service.documentos;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.repository.DocumentoRepository;
import unach.sindicato.api.repository.UddRepository;
import unach.sindicato.api.service.persistence.PersistenceService;
import unach.sindicato.api.utils.errors.CollectionNoActualizadaException;

@Service
@RequiredArgsConstructor
public class DocumentoService implements PersistenceService<Documento> {
    final DocumentoRepository repository;

    @Override
    public @NonNull UddRepository<Documento> repository() {
        return repository;
    }

    @Override
    public @NonNull Class<Documento> clazz() {
        return Documento.class;
    }

    @Transactional
    public Documento saveOrUpdate(@NonNull Documento documento) {
        System.out.println("ddddddddddddddddddddddddddddd " + documento.getClass());
        System.out.println(documento.getReporte().getMotivo());

        if (documento.getId() != null)
            if (repository.existsById(documento.getId()))
                if (!update(documento))
                    throw new CollectionNoActualizadaException(documento, getClass());
                else return findById(documento.getId());
        return repository.save(documento);
    }
}
