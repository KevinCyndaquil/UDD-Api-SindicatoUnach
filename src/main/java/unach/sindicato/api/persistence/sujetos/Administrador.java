package unach.sindicato.api.persistence.sujetos;

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
@Document(collection = "sujetos")
public class Administrador extends UddUser {

    @Override
    public @NonNull Roles getRol() {
        return Roles.ADMINISTRADOR;
    }

    @Override
    public String getUsername() {
        return "";
    }
}