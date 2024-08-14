package unach.sindicato.api.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;
import unach.sindicato.api.persistence.escuela.Maestro;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MaestroRepository extends UsuarioUDDRepository<Maestro> {
    @Override
    @Aggregation(pipeline = {
            "{$match: {'correo_institucional.direccion': ?0, '_class': ?1}}",
            "{$lookup: {from: 'documentos', localField: 'documentos.$id', foreignField: '_id', as: 'documentos'}}",
            "{$project: {'documentos.bytes': 0}}"
    })
    Maestro findByCorreo_institucional(String direccion, String _class);

    @Aggregation(pipeline = {
            "{$match: {'_id': ?0, '_class': ?1}}",
            "{$lookup: {from: 'documentos', localField: 'documentos.$id', foreignField: '_id', as: 'documentos'}}",
            "{$project: {'documentos.bytes': 0}}"
    })
    Optional<Maestro> findByIdExcludingBytes(ObjectId id, String _class);

    @Aggregation(pipeline = {
            "{$match: {'_class': ?0}}",
            "{$lookup: {from: 'documentos', localField: 'documentos.$id', foreignField: '_id', as: 'documentos'}}",
            "{$project: {'documentos.bytes': 0}}"
    })
    List<Maestro> findAllExcludingBytes(String _class);

    Set<Maestro> findByFacultadId(ObjectId facultadId);
    Set<Maestro> findByEstatus(Maestro.Estatus estatus);
    Set<Maestro> findByFacultadCampus(String facultadCampus);
}
