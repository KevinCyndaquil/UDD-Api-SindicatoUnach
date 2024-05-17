package unach.sindicato.api.persistence.documentos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import unach.sindicato.api.persistence.escuela.Maestro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@ToString(exclude = "bytes", callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "documentos")
public class Pdf extends Documento {
    byte[] bytes;

    @Override
    public Contents getContent() {
        return Contents.pdf;
    }

    public String generateName(@NonNull Maestro maestro) {
        return "%s_%s_%s-%s"
                .formatted(maestro.getNombre(),
                        maestro.getApellido_paterno(),
                        formato.name(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}