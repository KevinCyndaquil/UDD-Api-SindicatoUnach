package unach.sindicato.api.service.auth;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import unach.sindicato.api.persistence.escuela.UddAdmin;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.persistence.data.RolesUsuario;
import unach.sindicato.api.persistence.escuela.UsuarioUDD;

@Service
@RequiredArgsConstructor
public class UsuarioUDDService implements UserDetailsService {
    final MongoTemplate mongoTemplate;

    public @NonNull UsuarioUDD readById(@NonNull ObjectId id)
            throws UsernameNotFoundException {
        Query query = new Query(
                Criteria.where("_id").is(id)
                        .orOperator(
                                Criteria.where("_class").is(Maestro.class.getName()),
                                Criteria.where("_class").is(UddAdmin.class.getName())
                        ));
        UsuarioUDD user = mongoTemplate.findOne(query, UsuarioUDD.class);

        if (user == null) throw new UsernameNotFoundException("ID provided %s was not found"
                .formatted(id));
        return user;
    }

    public @NonNull UsuarioUDD readById(@NonNull ObjectId id, RolesUsuario rolExpected)
            throws UsernameNotFoundException {
        Query query = new Query(
                Criteria.where("_id").is(id)
                        .orOperator(
                                Criteria.where("_class").is(Maestro.class.getName()),
                                Criteria.where("_class").is(UddAdmin.class.getName())
                        )
                        .andOperator(Criteria.where("rol").is(rolExpected)));
        UsuarioUDD user = mongoTemplate.findOne(query, UsuarioUDD.class);

        if (user == null) throw new UsernameNotFoundException("ID provided %s was not found"
                .formatted(id));
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return readById(new ObjectId(username));
    }
}
