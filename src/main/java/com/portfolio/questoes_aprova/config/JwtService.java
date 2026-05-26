package com.portfolio.questoes_aprova.config;

import com.portfolio.questoes_aprova.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class JwtService {

    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    @Value("${jwt.secret:dev-secret-256bits-mude-em-producao}")
    private String secret;

    @Value("${jwt.expiration-seconds:86400}")
    private long expirationSeconds;

    public String gerarToken(Usuario usuario) {
        long exp = Instant.now().plusSeconds(expirationSeconds).getEpochSecond();
        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = encode("""
                {"sub":"%s","role":"%s","uid":"%s","exp":%d}
                """.formatted(usuario.getEmail(), usuario.getRole().name(), usuario.getUuid(), exp).trim());
        return header + "." + payload + "." + assinar(header + "." + payload);
    }

    public Optional<String> extrairEmailValido(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3 || !assinar(parts[0] + "." + parts[1]).equals(parts[2])) {
                return Optional.empty();
            }
            String payload = new String(URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
            long exp = Long.parseLong(extractJsonValue(payload, "exp"));
            if (Instant.now().getEpochSecond() > exp) {
                return Optional.empty();
            }
            return Optional.ofNullable(extractJsonValue(payload, "sub"));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    private String encode(String json) {
        return URL_ENCODER.encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    private String assinar(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao assinar JWT.", ex);
        }
    }

    private String extractJsonValue(String json, String key) {
        String marker = "\"" + key + "\":";
        int start = json.indexOf(marker);
        if (start < 0) {
            return null;
        }
        int valueStart = start + marker.length();
        if (json.charAt(valueStart) == '"') {
            int textStart = valueStart + 1;
            return json.substring(textStart, json.indexOf('"', textStart));
        }
        int end = json.indexOf(',', valueStart);
        if (end < 0) {
            end = json.indexOf('}', valueStart);
        }
        return json.substring(valueStart, end).trim();
    }
}
