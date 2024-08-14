package unach.sindicato.api.service.persistence;

import lombok.NonNull;
import org.bson.types.ObjectId;
import unach.sindicato.api.service.UddService;
import unach.sindicato.api.utils.exceptions.BusquedaSinResultadoException;
import unach.sindicato.api.utils.persistence.InstanciaUnica;
import unach.sindicato.api.utils.persistence.Unico;

import java.util.List;

public interface FindService <C extends Unico> extends UddService<C> {

    default C findById(ObjectId id) throws BusquedaSinResultadoException {
        return repository().findById(id)
                .orElseThrow(() -> new BusquedaSinResultadoException(clazz(), "_id", id));
    }

    default C findById(@NonNull C c) throws BusquedaSinResultadoException {
        return findById(c.getId());
    }

    default C findById(@NonNull InstanciaUnica cInstance) throws BusquedaSinResultadoException {
        return findById(cInstance.getId());
    }

    default List<C> findAll() {
        return repository().findAll(clazz().getName());
    }
}
