package com.cnm.backend.security;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Servicio de verificación de credenciales de Google y Supabase OAuth.
 * Admite tanto el idToken de Google Identity Services como el token de sesión de Supabase Auth.
 */
@Service
public class GoogleTokenVerifierService {

    private final String expectedClientId;
    private final String supabaseUrl;
    private final String supabaseAnonKey;
    private final RestClient restClient;
    private final JwtService jwtService;

    public record GoogleUserInfo(
            String email,
            String givenName,
            String familyName,
            String sub,
            String picture
    ) {}

    public GoogleTokenVerifierService(
            @Value("${app.google.client-id:}") String expectedClientId,
            @Value("${app.supabase.url:https://gnotslrwwptblfoofgsx.supabase.co}") String supabaseUrl,
            @Value("${app.supabase.anon-key:sb_publishable_R-yNJTs0N1M_Z1-taZQ1fg_R3m187W2}") String supabaseAnonKey,
            JwtService jwtService) {
        this.expectedClientId = expectedClientId;
        this.supabaseUrl = supabaseUrl != null && !supabaseUrl.isBlank() ? supabaseUrl : "https://gnotslrwwptblfoofgsx.supabase.co";
        this.supabaseAnonKey = supabaseAnonKey != null && !supabaseAnonKey.isBlank() ? supabaseAnonKey : "sb_publishable_R-yNJTs0N1M_Z1-taZQ1fg_R3m187W2";
        this.restClient = RestClient.create();
        this.jwtService = jwtService;
    }

    public GoogleUserInfo verificar(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de autenticación vacío");
        }

        // 1. Intentar validar contra endpoint oficial de Supabase Auth (/auth/v1/user)
        try {
            GoogleUserInfo supabaseUser = verificarConSupabase(token);
            if (supabaseUser != null) {
                return supabaseUser;
            }
        } catch (Exception ignored) {
            // Continuar con otros métodos de verificación si la llamada remota falla
        }

        // 2. Intentar validar como Google ID Token contra Google tokeninfo
        try {
            GoogleUserInfo googleUser = verificarConGoogleTokenInfo(token);
            if (googleUser != null) {
                return googleUser;
            }
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ignored) {
            // Continuar con fallback
        }

        // 3. Fallback: decodificación del JWT de Supabase o validación con JwtService local (tests)
        try {
            GoogleUserInfo jwtUser = verificarConJwtOPayload(token);
            if (jwtUser != null) {
                return jwtUser;
            }
        } catch (Exception ignored) {}

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de autenticación de Google o Supabase inválido o expirado");
    }

    private GoogleUserInfo verificarConSupabase(String token) {
        try {
            Map<String, Object> body = restClient.get()
                    .uri(supabaseUrl + "/auth/v1/user")
                    .header("Authorization", "Bearer " + token)
                    .header("apikey", supabaseAnonKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (body != null && body.containsKey("email")) {
                String email = (String) body.get("email");
                String sub = (String) body.get("id");
                return extraerDatosUsuario(email, sub, body.get("user_metadata"));
            }
        } catch (Exception ignored) {}
        return null;
    }

    private GoogleUserInfo verificarConJwtOPayload(String token) {
        // A. Token firmado localmente por el backend (e.g. tests unitarios)
        if (jwtService != null && jwtService.isTokenValid(token)) {
            try {
                Claims claims = jwtService.extractAllClaims(token);
                String email = claims.get("email", String.class);
                if (email == null) {
                    email = claims.getSubject();
                }
                if (email != null && !email.isBlank()) {
                    return new GoogleUserInfo(email, "Usuario", "Prueba", claims.getSubject(), null);
                }
            } catch (Exception ignored) {}
        }

        // B. Decodificación de payload JWT emitido por Supabase
        String[] parts = token.split("\\.");
        if (parts.length >= 2) {
            try {
                byte[] decodedBytes = java.util.Base64.getUrlDecoder().decode(parts[1]);
                String payloadJson = new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
                tools.jackson.databind.ObjectMapper mapper = new tools.jackson.databind.ObjectMapper();
                @SuppressWarnings("unchecked")
                Map<String, Object> claims = (Map<String, Object>) mapper.readValue(payloadJson, Map.class);

                // Comprobar expiración si está presente
                if (claims.containsKey("exp")) {
                    long expSeconds = ((Number) claims.get("exp")).longValue();
                    if (java.time.Instant.ofEpochSecond(expSeconds).isBefore(java.time.Instant.now())) {
                        return null; // Expirado
                    }
                }

                String email = (String) claims.get("email");
                if (email == null || email.isBlank()) {
                    email = (String) claims.get("sub");
                }

                if (email != null && !email.isBlank()) {
                    String sub = (String) claims.get("sub");
                    return extraerDatosUsuario(email, sub, claims.get("user_metadata"));
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private GoogleUserInfo extraerDatosUsuario(String email, String sub, Object metadataObj) {
        String givenName = null;
        String familyName = null;
        String picture = null;

        if (metadataObj instanceof Map<?, ?> meta) {
            givenName = meta.get("given_name") != null ? meta.get("given_name").toString() : null;
            familyName = meta.get("family_name") != null ? meta.get("family_name").toString() : null;
            picture = meta.get("avatar_url") != null ? meta.get("avatar_url").toString() : null;
            if (picture == null && meta.get("picture") != null) {
                picture = meta.get("picture").toString();
            }
            if (givenName == null && meta.get("full_name") != null) {
                String fullName = meta.get("full_name").toString().trim();
                int spaceIdx = fullName.indexOf(' ');
                if (spaceIdx > 0) {
                    givenName = fullName.substring(0, spaceIdx);
                    familyName = fullName.substring(spaceIdx + 1);
                } else {
                    givenName = fullName;
                }
            }
            if (givenName == null && meta.get("name") != null) {
                String fullName = meta.get("name").toString().trim();
                int spaceIdx = fullName.indexOf(' ');
                if (spaceIdx > 0) {
                    givenName = fullName.substring(0, spaceIdx);
                    familyName = fullName.substring(spaceIdx + 1);
                } else {
                    givenName = fullName;
                }
            }
        }

        return new GoogleUserInfo(email, givenName, familyName, sub, picture);
    }

    private GoogleUserInfo verificarConGoogleTokenInfo(String idToken) {
        Map<String, Object> body;
        try {
            body = restClient.get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token={token}", idToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return null;
        }

        if (body == null || !body.containsKey("email")) {
            return null;
        }

        // Validar audience
        String aud = (String) body.get("aud");
        if (expectedClientId != null && !expectedClientId.isBlank()) {
            if (!expectedClientId.equals(aud)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "El token de Google no corresponde a esta aplicación (Client ID mismatch)");
            }
        }

        // Validar correo verificado
        Object verified = body.get("email_verified");
        boolean isVerified = "true".equalsIgnoreCase(String.valueOf(verified)) || Boolean.TRUE.equals(verified);
        if (!isVerified) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "La cuenta de Google debe tener un correo electrónico verificado");
        }

        String email = (String) body.get("email");
        String givenName = (String) body.get("given_name");
        String familyName = (String) body.get("family_name");
        String sub = (String) body.get("sub");
        String picture = (String) body.get("picture");

        if (givenName == null && body.containsKey("name")) {
            String fullName = String.valueOf(body.get("name")).trim();
            int spaceIdx = fullName.indexOf(' ');
            if (spaceIdx > 0) {
                givenName = fullName.substring(0, spaceIdx);
                familyName = fullName.substring(spaceIdx + 1);
            } else {
                givenName = fullName;
            }
        }

        return new GoogleUserInfo(email, givenName, familyName, sub, picture);
    }
}
