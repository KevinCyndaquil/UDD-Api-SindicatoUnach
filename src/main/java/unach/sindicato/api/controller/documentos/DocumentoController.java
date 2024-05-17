package unach.sindicato.api.controller.documentos;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unach.sindicato.api.controller.PersistenceController;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.service.documentos.DocumentoService;

@RestController
@RequestMapping("documentos")
@RequiredArgsConstructor
public class DocumentoController implements PersistenceController<Documento> {
    final DocumentoService service;

    @Override
    public @NonNull DocumentoService service() {
        return service;
    }
}
