package com.acp.simccs.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class SecurityService {

    @Value("${application.security.encryption.secret-key}")
    private String secretKeyString;

    private static final String ALGORITHM_GCM = "AES/GCM/NoPadding";
    private static final String ALGORITHM_LEGACY = "AES";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final String VERSION_PREFIX = "v2:";

    // Legacy fallback key for data created before GCM migration
    private static final String LEGACY_KEY_RAW = "12345678901234567890123456789012";

    public String encrypt(String value) {
        if (value == null)
            return null;
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new java.security.SecureRandom().nextBytes(iv);

            byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM_GCM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            ByteBuffer bb = ByteBuffer.allocate(iv.length + encrypted.length);
            bb.put(iv);
            bb.put(encrypted);

            return VERSION_PREFIX + Base64.getEncoder().encodeToString(bb.array());
        } catch (Exception e) {
            throw new RuntimeException("Error executing encryption", e);
        }
    }

    public String decrypt(String value) {
        if (value == null)
            return null;
        try {
            if (value.startsWith(VERSION_PREFIX)) {
                return decryptGcm(value.substring(VERSION_PREFIX.length()));
            } else {
                return decryptLegacy(value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error executing decryption", e);
        }
    }

    private String decryptGcm(String base64Value) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(base64Value);
        ByteBuffer bb = ByteBuffer.wrap(decoded);

        byte[] iv = new byte[GCM_IV_LENGTH];
        bb.get(iv);

        byte[] ciphertext = new byte[bb.remaining()];
        bb.get(ciphertext);

        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM_GCM);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

        return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
    }

    private String decryptLegacy(String value) throws Exception {
        byte[] encryptedData = Base64.getDecoder().decode(value);

        // 1. Try current configured key (Assuming it's Base64 encoded)
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
            return decryptWithKey(encryptedData, keyBytes);
        } catch (Exception e) {
            // If current key fails or isn't Base64, system might be in a transitional state
        }

        // 2. Fallback to legacy hardcoded key (Raw bytes)
        try {
            return decryptWithKey(encryptedData, LEGACY_KEY_RAW.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new Exception("Legacy decryption failed for both current and fallback keys");
        }
    }

    private String decryptWithKey(byte[] encryptedData, byte[] keyBytes) throws Exception {
        SecretKey key = new SecretKeySpec(keyBytes, ALGORITHM_LEGACY);
        Cipher cipher = Cipher.getInstance(ALGORITHM_LEGACY);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedByteValue = cipher.doFinal(encryptedData);
        return new String(decryptedByteValue, StandardCharsets.UTF_8);
    }
}