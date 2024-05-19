package unach.sindicato.api.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;
import unach.sindicato.api.persistence.escuela.Maestro;

import java.util.Set;

@Repository
public interface MaestroRepository extends UddUserRepository<Maestro> {
    @Override
    @Aggregation(pipeline = {
            "{$match: {'correo_institucional.direccion': ?0, '_class': ?1}}",
            "{$lookup: {from: 'documentos', localField: 'documentos.$id', foreignField: '_id', as: 'documentos'}}",
            "{$project: {'documentos.bytes': 0}}"
    })
    Maestro findByCorreo_institucional(String direccion, String _class);

    Set<Maestro> findByFacultadId(ObjectId facultadId);
    Set<Maestro> findByEstatus(Maestro.Estatus estatus);
    Set<Maestro> findByFacultadCampus(String facultadCampus);
}
