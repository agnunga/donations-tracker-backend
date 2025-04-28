package io.omosh.dts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class HelperUtil {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generate() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 20; i++) {
            if (i > 0 && i % 5 == 0) {
                sb.append("-");
            }
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    public static String encryptCredential(String password, String base64Cert) throws Exception {
        byte[] decodedCert = Base64.getDecoder().decode(base64Cert);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedCert);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    @SneakyThrows
    public static String encryptPassword(String password) {
        // Load the certificate
        FileInputStream fis = new FileInputStream("src/main/resources/cert.cer");
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(fis);
        fis.close();

        // Extract the public key
        PublicKey publicKey = certificate.getPublicKey();

        // Encrypt the password using RSA
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(password.getBytes("UTF-8"));

        // Return Base64-encoded string
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    public static String getSecurityCredentials2(String initiatorPassword) {
        try {
            // Load certificate from resources
            Resource resource = new ClassPathResource("cert.cer");
            InputStream inputStream = resource.getInputStream();

            // Generate certificate and extract public key
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(inputStream);
            PublicKey publicKey = certificate.getPublicKey();

            // Initialize RSA cipher
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // Encrypt the password
            byte[] encryptedBytes = cipher.doFinal(initiatorPassword.getBytes("UTF-8"));

            // Return Base64 encoded string
            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            // Use your preferred logging framework
            System.err.println("Error generating security credentials: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String toBase64(String secretKey) throws Exception {
        String b64String = Base64.getEncoder().encodeToString(secretKey.getBytes());
        // System.out.println(b64String);
        return b64String;
    }

    public static String formatDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(formatter);
    }

    public static String formattedCurrentDateTime() {
        return formatDateTime(LocalDateTime.now());
    }

    public static String toJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }
}
