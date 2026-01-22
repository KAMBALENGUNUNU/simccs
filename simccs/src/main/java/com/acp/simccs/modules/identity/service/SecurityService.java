package com.acp.simccs.modules.identity.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class SecurityService {

    // In production, inject this from application.properties
    // This key must be 16, 24, or 32 bytes (128, 192, or 256 bits)
    private static final String SECRET_KEY_STRING = "12345678901234567890123456789012"; 
    private static final String ALGORITHM = "AES";

    public String encrypt(String value) {
        try {
            SecretKey key = new SecretKeySpec(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedByteValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedByteValue);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while encrypting data", e);
        }
    }

    public String decrypt(String value) {
        try {
            SecretKey key = new SecretKeySpec(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedByteValue = cipher.doFinal(Base64.getDecoder().decode(value));
            return new String(decryptedByteValue, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while decrypting data", e);
        }
    }
}