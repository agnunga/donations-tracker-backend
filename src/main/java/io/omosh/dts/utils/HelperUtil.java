package io.omosh.dts.utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
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


    public static String toBase64(String secretKey) throws Exception {
        String b64String = Base64.getEncoder().encodeToString(secretKey.getBytes());
        // System.out.println(b64String);
        return b64String;
    }

}
