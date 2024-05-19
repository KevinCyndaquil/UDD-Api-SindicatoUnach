package unach.sindicato.api.service.persistence;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;
import unach.sindicato.api.service.UddService;
import unach.sindicato.api.utils.error.NombrableRepetidoException;
import unach.sindicato.api.utils.persistence.Nombrable;
import unach.sindicato.api.utils.persistence.Unico;

import java.util.Set;
import java.util.stream.Collectors;

public interface SaveService <C extends Unico> extends UddService<C> {

    @Transactional
    default C save(@NonNull C c) throws NombrableRepetidoException, DuplicateKeyException, MongoWriteException {
        if (c instanceof Nombrable nombrable)
            if (repository().findByNombre(nombrable.getNombre(), clazz().getName()).isPresent())
                throw new NombrableRepetidoException(nombrable);
        return repository().insert(c);
    }

    @Transactional
    default Set<C> save(@NonNull Set<C> cs) throws NombrableRepetidoException, DuplicateKeyException {
        return cs.stream()
                .map(this::save)
                .collect(Collectors.toSet());
    }
}
