package unach.sindicato.api.service;

import lombok.NonNull;
import unach.sindicato.api.repository.UddRepository;
import unach.sindicato.api.utils.persistence.Unico;

/**
 * @author Kevin Alejandro Francisco González
 * Servicio generalizado para otros servicios de la API de UDD.
 * @param <C> tipo elemental de la colección de este Servicio.
 */
public interface UddService <C extends Unico> {
    /**
     * @return el repositorio de este servicio.
     */
    @NonNull UddRepository<C> repository();

    /**
     * @return el tipo elemental de este servicio.
     */
    @NonNull Class<C> clazz();
}
