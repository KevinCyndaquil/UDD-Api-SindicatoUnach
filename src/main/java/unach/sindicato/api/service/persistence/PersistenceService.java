package unach.sindicato.api.service.persistence;

import com.mongodb.DuplicateKeyException;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.transaction.annotation.Transactional;
import unach.sindicato.api.service.UddService;
import unach.sindicato.api.utils.errors.BusquedaSinResultadoException;
import unach.sindicato.api.utils.errors.NombrableRepetidoException;
import unach.sindicato.api.utils.persistence.InstanciaUnica;
import unach.sindicato.api.utils.persistence.Nombrable;
import unach.sindicato.api.utils.persistence.Unico;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio de persistencia de datos generalizado para colecciones de UDD API.
 * @param <C> tipo elemental de la colección de este servicio.
 */
public interface PersistenceService <C extends Unico> extends UddService<C>, SaveService<C>, FindService<C> {

    @Transactional
    default C save(@NonNull C c) throws NombrableRepetidoException, DuplicateKeyException {
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

    default C findById(ObjectId id) throws BusquedaSinResultadoException {
        return repository().findById(id)
                .orElseThrow(() -> new BusquedaSinResultadoException(clazz(), "_id", id));
    }

    default C findById(@NonNull C c) throws BusquedaSinResultadoException {
        return findById(c.getId());
    }

    default C findById(@NonNull InstanciaUnica<ObjectId> cInstance) throws BusquedaSinResultadoException {
        return findById(cInstance.getId());
    }

    default List<C> findAll() {
        return repository().findAll(clazz().getName());
    }

    default boolean update(@NonNull C c) {
        if (repository().existsById(c.getId())) return false;
        repository().save(c);
        return true;
    }

    default boolean delete(ObjectId id) {
        repository().deleteById(id);
        return !repository().existsById(id);
    }
}
