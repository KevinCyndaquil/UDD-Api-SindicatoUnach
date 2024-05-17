package unach.sindicato.api.utils;

import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class Encrytor {
    /**
     * Genera una salt aleatoria.
     * @return La sal generada.
     */
    private static @NonNull String generateSalt() {
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
    private static @NonNull String hashPasswordWithSalt(@NonNull String password, @NonNull String salt)
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
}
