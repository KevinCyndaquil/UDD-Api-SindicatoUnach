package unach.sindicato.api.controller.documentos;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import unach.sindicato.api.controller.PersistenceController;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.persistence.documentos.Pdf;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.service.documentos.DocumentoService;
import unach.sindicato.api.service.escuela.MaestroService;
import unach.sindicato.api.utils.groups.IdInfo;

@RestController
@RequestMapping("documentos")
@RequiredArgsConstructor
public class DocumentoController implements PersistenceController<Documento> {
    final DocumentoService service;
    final MaestroService maestroService;

    @Override
    public @NonNull DocumentoService service() {
        return service;
    }

    @PostMapping("as-pdf")
    public ResponseEntity<byte[]> generateAsPdf(
            @RequestBody@Validated(IdInfo.class) Documento.Entrada entrada) {
        Pdf pdf = (Pdf) service.findById(entrada.documento());
        Maestro maestro = maestroService.findById(entrada.maestro());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .name("pdf")
                .filename(pdf.generateName(maestro))
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf.getBytes());
    }
}
