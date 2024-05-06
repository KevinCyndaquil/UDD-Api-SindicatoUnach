package unach.sindicato.api.utils.persistence;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import unach.sindicato.api.utils.UddUser;

@Data
public class Credential {
    @NotNull ObjectId id;
    @NotEmpty String password;

    public static @NonNull Credential of(@NonNull UddUser user) {
        Credential credential = new Credential();
        credential.id = user.getId();
        credential.password = user.getPassword();
        return credential;
    }
}
