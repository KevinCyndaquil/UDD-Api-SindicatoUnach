package unach.sindicato.api.persistence.documentos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import unach.sindicato.api.persistence.escuela.Maestro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString(exclude = "bytes", callSuper = true)
@Document(collection = "documentos")
public class Pdf extends Documento {
    protected byte[] bytes;
    @JsonIgnore
    protected boolean encrypted = false;

    @Override
    public TipoContenido getContent() {
        return TipoContenido.pdf;
    }

    public String generateName(@NonNull Maestro maestro) {
        return "%s_%s_%s-%s.pdf"
                .formatted(
                        maestro.getNombre(),
                        maestro.getApellido_paterno(),
                        formato.name(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}
