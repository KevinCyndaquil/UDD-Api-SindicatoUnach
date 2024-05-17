package unach.sindicato.api.controller.documentos;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unach.sindicato.api.controller.PersistenceController;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.persistence.documentos.Pdf;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.service.documentos.DocumentoService;
import unach.sindicato.api.utils.persistence.InstanciaUnica;
import unach.sindicato.util.JsonData;

@RestController
@RequestMapping("documentos")
@RequiredArgsConstructor
public class DocumentoController implements PersistenceController<Documento> {
    final DocumentoService service;

    @Override
    public @NonNull DocumentoService service() {
        return service;
    }

    @GetMapping("public/get/pdf")
    public ResponseEntity<byte[]> generateAsPdf(@RequestParam("id") ObjectId instanciaUnica) {
        Pdf pdf = (Pdf) service.findById(instanciaUnica);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .name("pdf")
                .filename(pdf.generateName(JsonData.MAESTROS.first(Maestro.class).orElseThrow()))
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf.getBytes());
    }
}
