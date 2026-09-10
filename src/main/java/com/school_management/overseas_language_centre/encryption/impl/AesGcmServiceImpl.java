package com.school_management.overseas_language_centre.encryption.impl;

import com.school_management.overseas_language_centre.encryption.AesGcmService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AesGcmServiceImpl implements AesGcmService {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;       // 96 bits
    private static final int TAG_LENGTH = 128;     // 128 bits

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public AesGcmServiceImpl(
            @Value("${security.aes-key}") String base64Key
    ) {
        System.out.println("AES KEY RECEIVED: " + base64Key);

        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalStateException("AES key is empty or missing");
        }

        byte[] keyBytes;

        try {
            keyBytes = Base64.getDecoder().decode(base64Key);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "AES key is not valid Base64",
                    e
            );
        }

        System.out.println("AES KEY LENGTH: " + keyBytes.length);

        if (keyBytes.length != 32) {
            throw new IllegalStateException(
                    "AES key must be exactly 32 bytes, but was "
                            + keyBytes.length
            );
        }

        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    @Override
    public String encrypt(String plainText) {
        try {
            // Generate a NEW IV for every encryption
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            GCMParameterSpec gcmSpec =
                    new GCMParameterSpec(TAG_LENGTH, iv);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    gcmSpec
            );

            byte[] encrypted =
                    cipher.doFinal(
                            plainText.getBytes(StandardCharsets.UTF_8)
                    );

            /*
             * Store:
             *
             * IV + encrypted data + authentication tag
             *
             * The IV does NOT need to be secret.
             */
            byte[] result = ByteBuffer
                    .allocate(iv.length + encrypted.length)
                    .put(iv)
                    .put(encrypted)
                    .array();

            return Base64.getEncoder()
                    .encodeToString(result);

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Failed to encrypt data",
                    e
            );
        }
    }

    @Override
    public String decrypt(String cipherText) {
        try {
            byte[] decoded =
                    Base64.getDecoder()
                            .decode(cipherText);

            // Extract IV
            byte[] iv = new byte[IV_LENGTH];

            System.arraycopy(
                    decoded,
                    0,
                    iv,
                    0,
                    IV_LENGTH
            );

            // Extract encrypted data + authentication tag
            byte[] encrypted =
                    new byte[decoded.length - IV_LENGTH];

            System.arraycopy(
                    decoded,
                    IV_LENGTH,
                    encrypted,
                    0,
                    encrypted.length
            );

            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);

            GCMParameterSpec gcmSpec =
                    new GCMParameterSpec(TAG_LENGTH, iv);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    gcmSpec
            );

            byte[] decrypted =
                    cipher.doFinal(encrypted);

            return new String(
                    decrypted,
                    StandardCharsets.UTF_8
            );

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Failed to decrypt data",
                    e
            );
        }
    }
}
