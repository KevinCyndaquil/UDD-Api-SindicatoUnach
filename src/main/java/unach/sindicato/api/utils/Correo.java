package unach.sindicato.api.utils;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import unach.sindicato.api.utils.groups.PostInfo;

@Data
public class Correo {
    @NotEmpty(message = "Se necesita una dirección de correo electrónico",
            groups = PostInfo.class)
    @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$",
            message = "Correo electrónico invalido",
            groups = PostInfo.class)
    String direccion;
}
