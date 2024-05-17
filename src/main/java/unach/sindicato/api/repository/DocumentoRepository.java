package unach.sindicato.api.repository;

import org.springframework.stereotype.Repository;
import unach.sindicato.api.persistence.documentos.Documento;

@Repository
public interface DocumentoRepository extends UddRepository<Documento> {
}
