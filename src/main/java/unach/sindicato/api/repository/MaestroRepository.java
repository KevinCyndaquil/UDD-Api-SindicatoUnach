package unach.sindicato.api.repository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import unach.sindicato.api.persistence.escuela.Maestro;

import java.util.Set;

@Repository
public interface MaestroRepository extends UddUserRepository<Maestro> {
    Set<Maestro> findByFacultadId(ObjectId facultadId);
    Set<Maestro> findByEstatus(Maestro.Estatus estatus);
    Set<Maestro> findByFacultadCampus(String facultadCampus);
}
