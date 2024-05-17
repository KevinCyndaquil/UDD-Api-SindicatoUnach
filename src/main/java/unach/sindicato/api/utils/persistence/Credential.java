package unach.sindicato.api.utils.persistence;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import unach.sindicato.api.utils.Correo;
import unach.sindicato.api.utils.UddUser;

@Data
public class Credential {
    @NotNull@Valid Correo correo;
    @NotNull@NotBlank String password;

    public static @NonNull Credential by(@NonNull UddUser user) {
        Credential credential = new Credential();
        credential.correo = user.getCorreo_institucional();
        credential.password = user.getPassword();
        return credential;
    }
}
