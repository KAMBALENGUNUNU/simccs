package com.acp.simccs.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class SecurityServiceTest {

    private SecurityService securityService;
    private final String CURRENT_KEY_RAW = "currentKey1234567890123456789012"; // 32 bytes
    private final String CURRENT_KEY_BASE64 = Base64.getEncoder()
            .encodeToString(CURRENT_KEY_RAW.getBytes(StandardCharsets.UTF_8));
    private final String LEGACY_KEY_RAW = "12345678901234567890123456789012";

    @BeforeEach
    void setUp() {
        securityService = new SecurityService();
        ReflectionTestUtils.setField(securityService, "secretKeyString", CURRENT_KEY_BASE64);
    }

    @Test
    void testEncryptDecryptNewGcm() {
        String original = "Hello, GCM World!";
        String encrypted = securityService.encrypt(original);

        assertTrue(encrypted.startsWith("v2:"));
        assertEquals(original, securityService.decrypt(encrypted));
    }

    @Test
    void testDecryptLegacyWithCurrentKey() throws Exception {
        String original = "Legacy Data with Current Key";

        // Manually encrypt with ECB and current key
        SecretKey key = new SecretKeySpec(CURRENT_KEY_RAW.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(original.getBytes(StandardCharsets.UTF_8));
        String legacyEncrypted = Base64.getEncoder().encodeToString(encrypted);

        assertEquals(original, securityService.decrypt(legacyEncrypted));
    }

    @Test
    void testDecryptLegacyWithFallbackKey() throws Exception {
        String original = "Old Data from Before Migration";

        // Manually encrypt with ECB and the legacy hardcoded key
        SecretKey key = new SecretKeySpec(LEGACY_KEY_RAW.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(original.getBytes(StandardCharsets.UTF_8));
        String legacyEncrypted = Base64.getEncoder().encodeToString(encrypted);

        // This should fall back to the legacy key and succeed
        assertEquals(original, securityService.decrypt(legacyEncrypted));
    }

    @Test
    void testDecryptNull() {
        assertNull(securityService.decrypt(null));
    }

    @Test
    void testDecryptInvalid() {
        assertThrows(RuntimeException.class, () -> securityService.decrypt("not-base64-and-not-v2"));
    }
}
