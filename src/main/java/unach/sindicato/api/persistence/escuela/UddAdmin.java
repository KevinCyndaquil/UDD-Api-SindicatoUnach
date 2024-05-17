package unach.sindicato.api.persistence.escuela;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;
import unach.sindicato.api.utils.UddUser;
import unach.sindicato.api.utils.Roles;

@JsonTypeName("administrador")

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "escuela")
public class UddAdmin extends UddUser {

    @Override
    public @NonNull Roles getRol() {
        return Roles.administrador;
    }

    @Override
    public String getUsername() {
        return getCorreo_institucional().getDireccion();
    }
}
