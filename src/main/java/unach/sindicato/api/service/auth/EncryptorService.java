package unach.sindicato.api.service.auth;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import unach.sindicato.api.persistence.documentos.Pdf;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

@Service
public final class EncryptorService {
    @Value("${encryption.secret}") String SECRET;
    @Value("${encryption.iv}") String IV;

    /**
     * Genera una salt aleatoria.
     * @return La sal generada.
     */
    public static @NonNull String generateSalt() {
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
    public static @NonNull String hashPasswordWithSalt(@NonNull String password, @NonNull String salt)
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

    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256, SecureRandom.getInstanceStrong());
        return generator.generateKey();
    }

    private static @NonNull SecretKey generateKey(@NonNull String input) {
        return new SecretKeySpec(
                Base64.getDecoder().decode(input),
                "AES"
        );
    }

    private static byte @NonNull [] generateIV() {
        byte[] iv = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        return iv;
    }

    public byte[] encrypt(byte[] input)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        System.out.println(SECRET + " XDDDDDDDDDDDD");
        cipher.init(
                Cipher.ENCRYPT_MODE,
                generateKey(SECRET),
                new IvParameterSpec(Base64.getDecoder().decode(IV))
        );
        return cipher.doFinal(input);
    }

    public void encrypt(@NonNull Pdf pdf) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        pdf.setBytes(encrypt(pdf.getBytes()));
    }

    public byte[] decrypt(byte[] input)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(
                Cipher.DECRYPT_MODE,
                generateKey(SECRET),
                new IvParameterSpec(Base64.getDecoder().decode(IV))
        );
        return cipher.doFinal(input);
    }

    public void decrypt(@NonNull Pdf pdf) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        pdf.setBytes(decrypt(pdf.getBytes()));
    }

    public static void main(String[] args) throws Exception {
        SecretKey key = EncryptorService.generateKey();
        byte[] iv = EncryptorService.generateIV();

        String base64SecretKey = Base64.getEncoder().encodeToString(key.getEncoded());
        String base64Iv = Base64.getEncoder().encodeToString(iv);

        System.out.println("aes.secretKey=" + base64SecretKey);
        System.out.println("aes.iv=" + base64Iv);
    }
}
