package unach.sindicato.api.repository;

import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface UddRepository <C> extends MongoRepository<C, ObjectId> {

    @Query("{nombre: ?0, _class: ?1}")
    Optional<C> findByNombre(@NonNull String nombre, @NonNull String _class);

    @Query("{_class: ?0}")
    List<C> findAll(@NonNull String _class);

    @Query("{$or: ?0}")
    List<C> findAll(@NonNull Object[] matches);
}
