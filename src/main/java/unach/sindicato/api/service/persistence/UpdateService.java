package unach.sindicato.api.service.persistence;

import lombok.NonNull;
import unach.sindicato.api.service.UddService;
import unach.sindicato.api.utils.persistence.Unico;

public interface UpdateService <C extends Unico> extends UddService<C> {

    default boolean update(@NonNull C c) {
        if (!repository().existsById(c.getId())) return false;
        repository().save(c);
        return true;
    }
}
