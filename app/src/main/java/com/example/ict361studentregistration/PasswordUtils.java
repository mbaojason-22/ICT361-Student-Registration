
package com.example.ict361studentregistration;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtils {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;

    private PasswordUtils() {
        // Prevent creating instances of this utility class.
    }

    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        return Base64.encodeToString(
                salt,
                Base64.NO_WRAP
        );
    }

    public static String hashPassword(
            String password,
            String salt
    ) {
        PBEKeySpec spec = null;

        try {
            byte[] saltBytes = Base64.decode(
                    salt,
                    Base64.NO_WRAP
            );

            spec = new PBEKeySpec(
                    password.toCharArray(),
                    saltBytes,
                    ITERATIONS,
                    KEY_LENGTH
            );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            byte[] hash = factory
                    .generateSecret(spec)
                    .getEncoded();

            return Base64.encodeToString(
                    hash,
                    Base64.NO_WRAP
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not process password",
                    e
            );

        } finally {
            if (spec != null) {
                spec.clearPassword();
            }
        }
    }

    public static boolean verifyPassword(
            String password,
            String salt,
            String expectedHash
    ) {
        if (password == null
                || salt == null
                || expectedHash == null
                || salt.isEmpty()
                || expectedHash.isEmpty()) {
            return false;
        }

        try {
            byte[] actualHash = Base64.decode(
                    hashPassword(password, salt),
                    Base64.NO_WRAP
            );

            byte[] storedHash = Base64.decode(
                    expectedHash,
                    Base64.NO_WRAP
            );

            return MessageDigest.isEqual(
                    actualHash,
                    storedHash
            );

        } catch (Exception e) {
            return false;
        }
    }
}
