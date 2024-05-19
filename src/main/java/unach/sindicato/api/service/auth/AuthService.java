package unach.sindicato.api.service.auth;

import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;
import unach.sindicato.api.repository.UddUserRepository;
import unach.sindicato.api.service.persistence.FindService;
import unach.sindicato.api.service.persistence.SaveService;
import unach.sindicato.api.service.persistence.UpdateService;
import unach.sindicato.api.utils.Roles;
import unach.sindicato.api.utils.UddUser;
import unach.sindicato.api.utils.error.CredencialInvalidaException;
import unach.sindicato.api.utils.error.ProcesoEncriptacionException;
import unach.sindicato.api.utils.persistence.Credencial;
import unach.sindicato.api.utils.persistence.Token;

import java.security.NoSuchAlgorithmException;

/**
 * Servicio de autenticación genérico para la API de UDD.
 * @param <U> el tipo elemetal del UddUser de este servicio.
 */
public interface AuthService <U extends UddUser> extends SaveService<U>, FindService<U>, UpdateService<U> {

    @Override@NonNull UddUserRepository<U> repository();
    @NonNull JwtService jwtService();
    @NonNull Roles expectedRol();

    @Override
    default boolean update(@NonNull U u) {
        var uSaved = repository().findById(u.getId());
        if (uSaved.isEmpty()) return false;

        try {
            String salt = EncryptorService.generateSalt();
            String encryptedPassword = EncryptorService.hashPasswordWithSalt(u.getPassword(), salt);
            u.setPassword(encryptedPassword);
            u.setSalt(salt);
        } catch (NoSuchAlgorithmException e) {
            throw new ProcesoEncriptacionException(u);
        }

        repository().save(u);
        return true;
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
                .collection(user)
                .expires_in(jwtService().parse(token).getExpiration())
                .build();
    }

    default Token<U> login(@NonNull Credencial credencial) {
        U user = repository().findByCorreo_institucional(credencial.getCorreo().getDireccion(), clazz().getName());
        if (user == null)
            throw new CredencialInvalidaException(credencial, expectedRol());

        try {
            String encryptedPsswrd = EncryptorService.hashPasswordWithSalt(credencial.getPassword(), user.getSalt());

            if (!user.getPassword().equals(encryptedPsswrd))
                throw new CredencialInvalidaException(credencial, expectedRol(), "contraseña incorrecta");

            final String token = jwtService().generate(user);
            return Token.<U>builder()
                    .token(token)
                    .collection(user)
                    .expires_in(jwtService().parse(token).getExpiration())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new ProcesoEncriptacionException(user);
        }
    }
}
