package unach.sindicato.api.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import unach.sindicato.api.utils.UddUser;

@Repository
public interface UddUserRepository <U extends UddUser> extends UddRepository<U> {
    @Query("{'correo_institucional.direccion': ?0}")
    U findByCorreo_institucional(String direccion);
}
