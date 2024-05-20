package unach.sindicato.api.service.auth;

import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;
import unach.sindicato.api.repository.UddUserRepository;
import unach.sindicato.api.service.persistence.FindService;
import unach.sindicato.api.service.persistence.SaveService;
import unach.sindicato.api.service.persistence.UpdateService;
import unach.sindicato.api.utils.Roles;
import unach.sindicato.api.utils.UddUser;
import unach.sindicato.api.utils.error.BusquedaSinResultadoException;
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

        if (u.getPassword() != null) {
            try {
                System.out.println("Contraseña request en el update: " + u.getPassword());
                String salt = EncryptorService.generateSalt();
                String encryptedPassword = EncryptorService.hashPasswordWithSalt(u.getPassword(), salt);
                u.setPassword(encryptedPassword);
                u.setSalt(salt);
            } catch (NoSuchAlgorithmException e) {
                throw new ProcesoEncriptacionException(u);
            }
        } else {
            System.out.println("pasando la contraseña ya guardada");
            u.setPassword(uSaved.get().getPassword());
            u.setSalt(uSaved.get().getSalt());
        }

        repository().save(u);
        return true;
    }

    /**
     * Además de buscar por el ID, mantiene la contraseña que se proporcione en el parametro del método en caso de
     * haber una.
     * @param u el objeto con el ID a buscar, puede contener un password en caso de requerir no usar la guardada
     *                en la base de datos.
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
            String encryptedPsswrd = EncryptorService.hashPasswordWithSalt(credencial.getPassword(), user.getSalt());

            if (!user.getPassword().equals(encryptedPsswrd))
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
