package unach.sindicato.api.service.auth;

import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;
import unach.sindicato.api.repository.UsuarioUDDRepository;
import unach.sindicato.api.service.persistence.FindService;
import unach.sindicato.api.service.persistence.SaveService;
import unach.sindicato.api.service.persistence.UpdateService;
import unach.sindicato.api.persistence.data.RolesUsuario;
import unach.sindicato.api.persistence.escuela.UsuarioUDD;
import unach.sindicato.api.utils.exceptions.BusquedaSinResultadoException;
import unach.sindicato.api.utils.exceptions.CredencialInvalidaException;
import unach.sindicato.api.utils.exceptions.ProcesoEncriptacionException;
import unach.sindicato.api.utils.persistence.Credencial;
import unach.sindicato.api.utils.persistence.Token;

import java.security.NoSuchAlgorithmException;

/**
 * @param <U> el tipo elemental del UsuarioUDD de este servicio.
 * @author Kevin Alejandro Francisco González.
 * Servicio de autenticación genérico para la API de UDD.
 */
public interface AuthService<U extends UsuarioUDD> extends SaveService<U>, FindService<U>, UpdateService<U> {

    @Override
    @NonNull
    UsuarioUDDRepository<U> repository();

    @NonNull
    JwtService jwtService();

    @NonNull
    RolesUsuario expectedRol();

    @Override
    default boolean update(@NonNull U u) {
        var uSaved = repository().findById(u.getId());
        if (uSaved.isEmpty()) return false;

        if (u.getPassword() != null) {
            try {
                String salt = EncryptorService.generateSalt();
                String encryptedPassword = EncryptorService.hashPasswordWithSalt(u.getPassword(), salt);
                u.setPassword(encryptedPassword);
                u.setSalt(salt);
            } catch (NoSuchAlgorithmException e) {
                throw new ProcesoEncriptacionException(u);
            }
        } else {
            u.setPassword(uSaved.get().getPassword());
            u.setSalt(uSaved.get().getSalt());
        }

        repository().save(u);
        return true;
    }

    /**
     * Además de buscar por el ID, mantiene la contraseña que se proporcione en el parámetro del método en caso de
     * haber una.
     *
     * @param u el objeto con el ID a buscar, puede contener un password en caso de requerir no usar la guardada
     *          en la base de datos.
     * @return el objeto persistido.
     * @throws BusquedaSinResultadoException en caso de no hallar el objeto buscado.
     */
    @Override
    default U findById(@NonNull U u) throws BusquedaSinResultadoException {
        U uSaved = findById(u.getId());
        if (u.getPassword() != null) uSaved.setPassword(u.getPassword());
        return uSaved;
    }

    @Transactional
    default Token<U> register(@NonNull U u) {
        try {
            String salt = EncryptorService.generateSalt();
            final String encryptedPassword = EncryptorService.hashPasswordWithSalt(
                    u.getPassword(),
                    salt);
            u.setPassword(encryptedPassword);
            u.setSalt(salt);

        } catch (NoSuchAlgorithmException e) {
            throw new ProcesoEncriptacionException(u);
        }

        U user = save(u);
        String token = jwtService().generate(user);

        return Token.<U>builder()
                .token(token)
                .document(user)
                .expires_in(jwtService().parse(token).getExpiration())
                .build();
    }

    default Token<U> login(@NonNull Credencial credencial) {
        U user = repository().findByCorreo_institucional(credencial.getCorreo().getDireccion(), clazz().getName());
        if (user == null)
            throw new CredencialInvalidaException(credencial, expectedRol());

        try {
            String encryptedPwd = EncryptorService.hashPasswordWithSalt(credencial.getPassword(), user.getSalt());

            if (!user.getPassword().equals(encryptedPwd))
                throw new CredencialInvalidaException(credencial, expectedRol(), "contraseña incorrecta");

            final String token = jwtService().generate(user);
            return Token.<U>builder()
                    .token(token)
                    .document(user)
                    .expires_in(jwtService().parse(token).getExpiration())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new ProcesoEncriptacionException(user);
        }
    }
}
