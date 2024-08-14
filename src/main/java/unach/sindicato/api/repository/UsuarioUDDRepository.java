package unach.sindicato.api.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import unach.sindicato.api.persistence.escuela.UsuarioUDD;

@Repository
public interface UsuarioUDDRepository<U extends UsuarioUDD> extends UddRepository<U> {
    @Query("{'correo_institucional.direccion': ?0, _class:  ?1}")
    U findByCorreo_institucional(String direccion, String _class);
}
