package unach.sindicato.api.utils.persistence;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import unach.sindicato.api.persistence.data.Correo;
import unach.sindicato.api.persistence.escuela.UsuarioUDD;

@Data
public class Credencial {
    @NotNull(message = "Se requiere un correo para una credencial")@Valid Correo correo;
    @NotBlank(message = "Se requiere una contraseña") String password;

    public static @NonNull Credencial by(@NonNull UsuarioUDD user) {
        Credencial credencial = new Credencial();
        credencial.correo = user.getCorreo_institucional();
        credencial.password = user.getPassword();
        return credencial;
    }
}
