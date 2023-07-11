package fr.konoashi.proxyprovider.service.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class CryptUtil {

    private CryptUtil() {}

    public static PublicKey readPublicKey(byte[] encoded) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException("Failed to read public key", ex);
        }
    }

    public static SecretKey generateSharedSecret() {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128);
            return gen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Failed to generate shared secret", ex);
        }
    }

    public static byte[] encrypt(byte[] bytes, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(bytes);
        } catch (GeneralSecurityException ex) {
            throw new RuntimeException("Failed to encrypt using public key", ex);
        }
    }

}
