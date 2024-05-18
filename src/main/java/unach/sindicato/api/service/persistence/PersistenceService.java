package unach.sindicato.api.service.persistence;

import org.bson.types.ObjectId;
import unach.sindicato.api.utils.persistence.Unico;

/**
 * Servicio de persistencia de datos generalizado para colecciones de UDD API.
 * @param <C> tipo elemental de la colección de este servicio.
 */
public interface PersistenceService <C extends Unico> extends SaveService<C>, FindService<C>, UpdateService<C> {

    default boolean delete(ObjectId id) {
        repository().deleteById(id);
        return !repository().existsById(id);
    }
}
