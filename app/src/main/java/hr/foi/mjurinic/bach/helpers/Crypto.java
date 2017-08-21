package hr.foi.mjurinic.bach.helpers;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

public class Crypto {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16;

    public static byte[] encrypt(byte[] data, byte[] key) {
        try {
            byte[] iv = new byte[IV_SIZE];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(data);
            byte[] ivEncrypted = new byte[IV_SIZE + encrypted.length];

            // Prepends IV to encrypted data;
            System.arraycopy(iv, 0, ivEncrypted, 0, IV_SIZE);
            System.arraycopy(encrypted, 0, ivEncrypted, IV_SIZE, encrypted.length);

            return ivEncrypted;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            Timber.e(e);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            Timber.e(e);
        }

        return null;
    }

    public static byte[] decrypt(byte[] data, byte[] key) {
        try {
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(data, 0, iv, 0, IV_SIZE);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            byte[] encrypted = new byte[data.length - IV_SIZE];
            System.arraycopy(data, IV_SIZE, encrypted, 0, encrypted.length);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            return cipher.doFinal(encrypted);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            Timber.e(e);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            Timber.e(e);
        }

        return null;
    }

    public static byte[] generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(128);

            SecretKey secretKey = keyGenerator.generateKey();

            return secretKey.getEncoded();

        } catch (NoSuchAlgorithmException e) {
            Timber.e(e);
        }

        return null;
    }
}
