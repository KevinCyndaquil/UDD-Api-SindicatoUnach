package unach.sindicato.api.controller.documentos;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import unach.sindicato.api.controller.persistence.FindController;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.persistence.documentos.Pdf;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.service.documentos.DocumentoService;
import unach.sindicato.api.service.escuela.MaestroService;
import unach.sindicato.api.utils.groups.IdInfo;
import unach.sindicato.api.utils.response.UddResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("documentos")
@RequiredArgsConstructor
public class DocumentoController implements FindController<Documento> {
    final DocumentoService service;
    final MaestroService maestroService;

    @Override
    public @NonNull DocumentoService service() {
        return service;
    }

    @GetMapping("/estatus")
    public UddResponse getEstatus() {
        List<Map<String, String>> estatus = Arrays.stream(Documento.Estatus.values())
                .map(e -> Map.of("name", e.name(), "hex", e.hexColor))
                .toList();
        return UddResponse.collection()
                .message("Estatus documento encontrados correctamente")
                .status(HttpStatus.OK)
                .collection(estatus)
                .build();
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
