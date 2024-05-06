package unach.sindicato.api.utils.persistence;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public final class Token <C extends Unico> {
    C collection;
    String token;
    Date expires_in;
}
