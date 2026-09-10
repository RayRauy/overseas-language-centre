package com.school_management.overseas_language_centre.encryption.impl;

import com.school_management.overseas_language_centre.encryption.EncryptionService;
import com.school_management.overseas_language_centre.exceptions.EncryptionException;
import com.school_management.overseas_language_centre.property.EncryptionProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EncryptionServiceImpl implements EncryptionService {
    private static final String ALGORITHM = "AES/GCM/NoPadding"; // authenticated encryption
    private static final int AES_KEY_LENGTH = 32;   // AES-256 = 32 bytes
    private static final int GCM_IV_LENGTH = 12;    // recommended GCM IV size (96 bits)
    private static final int GCM_TAG_LENGTH = 128;  // auth tag bits (integrity)
    private static final int SALT_LENGTH = 16;      // HKDF salt length
    private static final String HMAC = "HmacSHA256"; // used inside HKDF
    // Domain separation so captcha keys differ from other features using the same master key
    private static final byte[] HKDF_INFO = "captcha-aes-gcm".getBytes(StandardCharsets.UTF_8);

    // Loaded from encryption.secret-key in application.yaml
    private final EncryptionProperties encryptionProperties;

    private final SecureRandom random = new SecureRandom();

    // Decoded master key bytes — wiped on shutdown
    private byte[] masterKey;


    /** Decode and validate the configured key; fail fast at startup if it is wrong. */
    //Method ដែលមាន @PostConstruct នឹងត្រូវបាន Spring ហៅដោយស្វ័យប្រវត្តិ បន្ទាប់ពី Bean ត្រូវបានបង្កើត និង Dependency Injection រួចរាល់។
    @PostConstruct
    public void init() {
        try {
            // Expect Base64 of exactly 32 raw bytes
            masterKey = Base64.getDecoder().decode(encryptionProperties.getSecretKey());
            if (masterKey.length != AES_KEY_LENGTH) {
                throw new IllegalArgumentException("AES-256 requires a 32-byte key");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Invalid encryption.secret-key — expected Base64 of 32 bytes (openssl rand -base64 32)", e);
        }
    }

    @Override
    public String encrypt(String plaintext) {
        validateInput(plaintext, "Plaintext");
        try {
            // Fresh salt + IV for every encrypt call
            byte[] salt = generateRandomBytes(SALT_LENGTH);
            byte[] iv = generateRandomBytes(GCM_IV_LENGTH);

            // Derive a one-off AES key from masterKey + salt (HKDF)
            SecretKey key = deriveKey(salt);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // GCMParameterSpec carries IV length + auth-tag length
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            // doFinal returns ciphertext || auth-tag
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Pack salt + iv + ciphertext into one Base64 blob for Redis
            return Base64.getEncoder().encodeToString(combine(salt, iv, encrypted));
        } catch (Exception e) {
            throw new EncryptionException("Encryption failed", e);
        }
    }

    @Override
    public String decrypt(String ciphertext) {
        validateInput(ciphertext, "Ciphertext");
        try {
            // Unpack Base64 → salt || iv || ciphertext+tag
            byte[] combined = Base64.getDecoder().decode(ciphertext);

            if (combined.length <= SALT_LENGTH + GCM_IV_LENGTH) {
                throw new EncryptionException("Invalid encrypted data format", null);
            }

            byte[] salt = Arrays.copyOfRange(combined, 0, SALT_LENGTH);
            byte[] iv = Arrays.copyOfRange(combined, SALT_LENGTH, SALT_LENGTH + GCM_IV_LENGTH);
            byte[] encrypted = Arrays.copyOfRange(combined, SALT_LENGTH + GCM_IV_LENGTH, combined.length);

            // Same HKDF path as encrypt — salt must match
            SecretKey key = deriveKey(salt);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            // Throws if the auth tag does not match (tampered data)
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EncryptionException("Decryption failed", e);
        }
    }

    /**
     * HKDF (RFC 5869) extract-then-expand: master key + salt → per-value AES key.
     * Simplified one-block expand (enough for 32-byte AES key).
     */
    private SecretKey deriveKey(byte[] salt) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(HMAC);

        // Extract: PRK = HMAC(salt, masterKey)
        mac.init(new SecretKeySpec(salt, HMAC));
        byte[] prk = mac.doFinal(masterKey);

        // Expand: OKM = HMAC(PRK, info || 0x01)
        mac.init(new SecretKeySpec(prk, HMAC));
        mac.update(HKDF_INFO);
        mac.update((byte) 1);
        byte[] okm = mac.doFinal();

        return new SecretKeySpec(okm, "AES");
    }

    /** Fill a new byte array with SecureRandom bytes. */
    private byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }

    /** Concatenate salt || iv || ciphertext into one array. */
    private byte[] combine(byte[] salt, byte[] iv, byte[] encrypted) {
        byte[] combined = new byte[salt.length + iv.length + encrypted.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(iv, 0, combined, salt.length, iv.length);
        System.arraycopy(encrypted, 0, combined, salt.length + iv.length, encrypted.length);
        return combined;
    }

    private void validateInput(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank");
        }
    }

    /** Best-effort wipe of the key material on shutdown. */
    @PreDestroy
    public void destroy() {
        if (masterKey != null) {
            Arrays.fill(masterKey, (byte) 0); // overwrite secret bytes in memory
        }
    }
}
