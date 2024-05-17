package unach.sindicato.api.service.persistence;

import lombok.NonNull;
import org.bson.types.ObjectId;
import unach.sindicato.api.utils.persistence.Unico;

/**
 * Servicio de persistencia de datos generalizado para colecciones de UDD API.
 * @param <C> tipo elemental de la colección de este servicio.
 */
public interface PersistenceService <C extends Unico> extends SaveService<C>, FindService<C> {

    default boolean update(@NonNull C c) {
        if (!repository().existsById(c.getId())) return false;
        repository().save(c);
        return true;
    }

    default boolean delete(ObjectId id) {
        repository().deleteById(id);
        return !repository().existsById(id);
    }
}
