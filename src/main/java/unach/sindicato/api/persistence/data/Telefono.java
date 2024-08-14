package unach.sindicato.api.persistence.data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class Telefono {
    @NotEmpty(message = "Se requiere el número telefónico")
    @Pattern(regexp = "^\\+\\(\\d{2}\\) \\d{3} \\d{3} \\d{4}|\\d{4} \\d{4}$", message = "Número telefónico invalido")
    String numero;
}
