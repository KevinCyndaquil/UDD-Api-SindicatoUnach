package unach.sindicato.api.utils.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class Token <D> {
    D document;
    String token;
    Date expires_in;
}
