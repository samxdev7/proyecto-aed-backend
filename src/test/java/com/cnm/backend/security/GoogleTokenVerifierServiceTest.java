package com.cnm.backend.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Verificación de Audiencia y Client ID de Google")
class GoogleTokenVerifierServiceTest {

    private static final String CLIENT_ID_1 = "504660215527-0gml5batfg23p6llcku19ss38f0lm3pr.apps.googleusercontent.com";
    private static final String CLIENT_ID_2 = "123456789012-abcdefghijklmnopqrstuvwxyz.apps.googleusercontent.com";

    @Test
    @DisplayName("Acepta audience idéntico al Client ID configurado")
    void esAudienceValido_conMismoClientId_retornaTrue() {
        GoogleTokenVerifierService service = new GoogleTokenVerifierService(
                CLIENT_ID_1, "", "", null);

        assertTrue(service.esAudienceValido(CLIENT_ID_1));
    }

    @Test
    @DisplayName("Rechaza audience si difiere del Client ID configurado (Client ID mismatch)")
    void esAudienceValido_conClientIdDiferente_retornaFalse() {
        GoogleTokenVerifierService service = new GoogleTokenVerifierService(
                CLIENT_ID_1, "", "", null);

        assertFalse(service.esAudienceValido(CLIENT_ID_2));
        assertFalse(service.esAudienceValido("otro-client-id-invalido.apps.googleusercontent.com"));
    }

    @Test
    @DisplayName("Sanitiza comillas simples, dobles y espacios en blanco en la configuración")
    void esAudienceValido_conComillasOEspaciosEnConfiguracion_retornaTrue() {
        // En Render o archivos .env a veces se pega con comillas: "504660215527-..." o con espacios
        GoogleTokenVerifierService serviceConComillasDobles = new GoogleTokenVerifierService(
                "\"" + CLIENT_ID_1 + "\"", "", "", null);
        assertTrue(serviceConComillasDobles.esAudienceValido(CLIENT_ID_1));

        GoogleTokenVerifierService serviceConEspacios = new GoogleTokenVerifierService(
                "  " + CLIENT_ID_1 + "  \n", "", "", null);
        assertTrue(serviceConEspacios.esAudienceValido(CLIENT_ID_1));
    }

    @Test
    @DisplayName("Soporta múltiples Client IDs separados por comas (e.g. web prod y preview/local)")
    void esAudienceValido_conMultiplesClientIds_retornaTrueParaCualquiera() {
        GoogleTokenVerifierService service = new GoogleTokenVerifierService(
                CLIENT_ID_1 + ", " + CLIENT_ID_2, "", "", null);

        assertTrue(service.esAudienceValido(CLIENT_ID_1));
        assertTrue(service.esAudienceValido(CLIENT_ID_2));
        assertFalse(service.esAudienceValido("cliente-tercero.apps.googleusercontent.com"));
    }

    @Test
    @DisplayName("Si no se define Client ID en la configuración, es permisivo")
    void esAudienceValido_sinConfiguracion_retornaTrue() {
        GoogleTokenVerifierService serviceVacio = new GoogleTokenVerifierService(
                "", "", "", null);
        assertTrue(serviceVacio.esAudienceValido(CLIENT_ID_1));

        GoogleTokenVerifierService serviceNulo = new GoogleTokenVerifierService(
                null, "", "", null);
        assertTrue(serviceNulo.esAudienceValido(CLIENT_ID_1));
    }

    @Test
    @DisplayName("Token nulo o en blanco lanza ResponseStatusException UNAUTHORIZED")
    void verificar_conTokenNuloOVacio_lanzaExcepcion() {
        GoogleTokenVerifierService service = new GoogleTokenVerifierService(
                CLIENT_ID_1, "", "", null);

        assertThrows(ResponseStatusException.class, () -> service.verificar(null));
        assertThrows(ResponseStatusException.class, () -> service.verificar("   "));
    }
}
