package unach.sindicato.api.service.auth;

import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;
import unach.sindicato.api.repository.UddUserRepository;
import unach.sindicato.api.service.persistence.FindService;
import unach.sindicato.api.service.persistence.SaveService;
import unach.sindicato.api.utils.UddUser;
import unach.sindicato.api.utils.errors.CredencialInvalidaException;
import unach.sindicato.api.utils.errors.ProcesoEncriptacionException;
import unach.sindicato.api.utils.persistence.Credential;
import unach.sindicato.api.utils.persistence.Token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Servicio de autenticación genérico para la API de UDD.
 * @param <U> el tipo elemetal del UddUser de este servicio.
 */
public interface AuthService <U extends UddUser> extends SaveService<U>, FindService<U> {

    @Override@NonNull UddUserRepository<U> repository();
    @NonNull JwtService jwtService();

    /**
     * Genera una salt aleatoria.
     * @return La sal generada.
     */
    private @NonNull String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return new String(salt, StandardCharsets.UTF_8);
    }

    /**
     * Encripta una contraseña con una salt.
     * @param password La contraseña a encriptar.
     * @param salt La salt a utilizar.
     * @return La contraseña encriptada.
     * @throws NoSuchAlgorithmException cuando el algoritmo usado no puede ser implementado.
     */
    private @NonNull String hashPasswordWithSalt(@NonNull String password, @NonNull String salt)
            throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] inputBytes = new byte[saltBytes.length + passwordBytes.length];

        System.arraycopy(saltBytes, 0, inputBytes, 0, saltBytes.length);
        System.arraycopy(passwordBytes, 0, inputBytes, saltBytes.length, passwordBytes.length);

        byte[] hash = digest.digest(inputBytes);
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }

        return hexString.toString();
    }

    @Transactional
    default Token<U> register(@NonNull U u) {
        try {
            String salt = generateSalt();
            final String encryptedPassword = hashPasswordWithSalt(
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

    default Token<U> login(@NonNull Credential credential) {
        U user = repository().findByCorreo_institucional(credential.getCorreo().getDireccion());

        try {
            String encryptedPsswrd = hashPasswordWithSalt(credential.getPassword(), user.getSalt());
            if (!user.getPassword().equals(encryptedPsswrd))
                throw new CredencialInvalidaException(credential);

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
