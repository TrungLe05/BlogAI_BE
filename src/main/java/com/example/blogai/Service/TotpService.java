package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.enums.ErrorCode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TotpService {

    static final String ISSUER = "BlogAI";

    public String generateSecret() {
        byte[] bytes = new byte[20];
        new SecureRandom().nextBytes(bytes);
        return new Base32().encodeToString(bytes).replace("=", "");
    }

    public String buildOtpauthUri(String email, String secret) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                ISSUER,
                URLEncoder.encode(email, StandardCharsets.UTF_8),
                secret,
                ISSUER
        );
    }

    public String generateQrBase64(String otpauthUri) {
        try {
            BitMatrix matrix = new QRCodeWriter()
                    .encode(otpauthUri, BarcodeFormat.QR_CODE, 300, 300);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            log.error("QR generation failed", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public boolean verifyCode(String secret, String inputCode) {
        long timeStep = System.currentTimeMillis() / 1000L / 30;
        for (int delta = -1; delta <= 2; delta++) {
            try {
                if (generateTotp(secret, timeStep + delta).equals(inputCode)) {
                    return true;
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    private String generateTotp(String secret, long timeStep) throws Exception {
        byte[] key  = new Base32().decode(secret.toUpperCase());
        byte[] data = ByteBuffer.allocate(8).putLong(timeStep).array();

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key, "HmacSHA1"));
        byte[] hash = mac.doFinal(data);

        int offset = hash[hash.length - 1] & 0x0F;
        int otp = ((hash[offset]     & 0x7F) << 24)
                | ((hash[offset + 1] & 0xFF) << 16)
                | ((hash[offset + 2] & 0xFF) << 8)
                |  (hash[offset + 3] & 0xFF);

        return String.format("%06d", otp % 1_000_000);
    }
}
