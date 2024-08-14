package unach.sindicato.api.persistence.escuela;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;
import unach.sindicato.api.persistence.data.RolesUsuario;

/**
 * @author Kevin Alejandro Francisco González
 * Encargado de admistrar a los demás usuarios a nivel general.
 * Tiene todos los permisos necesarios para realizar cualquier acción.
 */

@JsonTypeName("administrador")

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "escuela")
public class UddAdmin extends UsuarioUDD {

    @Override
    public @NonNull RolesUsuario getRol() {
        return RolesUsuario.administrador;
    }

    @Override
    public String getUsername() {
        return getCorreo_institucional().getDireccion();
    }
}
