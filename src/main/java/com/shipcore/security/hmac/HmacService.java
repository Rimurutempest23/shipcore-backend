package com.shipcore.security.hmac;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class HmacService {

    private static final String HMAC_SHA256 = "HmacSHA256";

    @Value("${jwt.secret:U2hpcENvcmUyMDI2U3VwZXJTZWNyZXRLZXlGb3JKV1RUb2tlbkdlbmVyYXRpb24xMjM0NTY3ODkw}")
    private String defaultSecretPhrase;

    /**
     * Genera una firma HMAC-SHA256 a partir de un payload y una frase secreta / clave.
     */
    public String calculateHmac(String payload, String secretKey) {
        try {
            String keyToUse = (secretKey != null && !secretKey.isBlank()) ? secretKey : defaultSecretPhrase;
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyToUse.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKeySpec);

            byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Error al calcular la firma HMAC-SHA256", e);
        }
    }

    /**
     * Firma un payload usando la frase secreta global de la aplicacion.
     */
    public String calculateHmac(String payload) {
        return calculateHmac(payload, defaultSecretPhrase);
    }

    /**
     * Verifica la validez de una firma HMAC-SHA256 utilizando comparacion en tiempo constante para prevenir Timing Attacks.
     */
    public boolean verifyHmac(String payload, String secretKey, String expectedHmac) {
        if (payload == null || expectedHmac == null) {
            return false;
        }

        String calculatedHmac = calculateHmac(payload, secretKey);
        byte[] a = calculatedHmac.getBytes(StandardCharsets.UTF_8);
        byte[] b = expectedHmac.getBytes(StandardCharsets.UTF_8);

        return MessageDigest.isEqual(a, b);
    }

    /**
     * Verifica la validez de una firma HMAC-SHA256 usando la frase secreta global.
     */
    public boolean verifyHmac(String payload, String expectedHmac) {
        return verifyHmac(payload, defaultSecretPhrase, expectedHmac);
    }

}
