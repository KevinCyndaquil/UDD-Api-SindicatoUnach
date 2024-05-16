package unach.sindicato.api.service.persistence;

import lombok.NonNull;
import org.bson.types.ObjectId;
import unach.sindicato.api.utils.errors.BusquedaSinResultadoException;
import unach.sindicato.api.utils.persistence.InstanciaUnica;
import unach.sindicato.api.utils.persistence.Unico;

import java.util.List;

public interface FindService <C extends Unico> {
    C findById(ObjectId id) throws BusquedaSinResultadoException;
    C findById(@NonNull C c) throws BusquedaSinResultadoException;
    C findById(@NonNull InstanciaUnica cInstance) throws BusquedaSinResultadoException;
    List<C> findAll();
}
