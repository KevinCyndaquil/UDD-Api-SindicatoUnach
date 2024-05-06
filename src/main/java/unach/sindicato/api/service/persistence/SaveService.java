package unach.sindicato.api.service.persistence;

import com.mongodb.DuplicateKeyException;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;
import unach.sindicato.api.utils.errors.NombrableRepetidoException;
import unach.sindicato.api.utils.persistence.Unico;

import java.util.Set;

public interface SaveService <C extends Unico> {
    @Transactional C save(@NonNull C c) throws NombrableRepetidoException, DuplicateKeyException;
    @Transactional Set<C> save(@NonNull Set<C> c) throws NombrableRepetidoException, DuplicateKeyException;
}
