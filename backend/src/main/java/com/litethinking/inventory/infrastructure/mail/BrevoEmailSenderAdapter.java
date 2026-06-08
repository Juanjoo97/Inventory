package com.litethinking.inventory.infrastructure.mail;

import com.litethinking.inventory.application.port.out.EmailSenderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Implementacion del puerto de envio de correo usando la API HTTP de Brevo.
 * Opera sobre HTTPS (puerto 443), por lo que NO se ve afectada por el bloqueo
 * de puertos SMTP (25/465/587) del plan gratuito de Render.
 *
 * Se activa con: app.mail.provider=brevo
 */
@Component
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "brevo")
public class BrevoEmailSenderAdapter implements EmailSenderPort {

    private static final String BREVO_ENDPOINT = "https://api.brevo.com/v3/smtp/email";

    private final RestClient restClient;
    private final String fromEmail;
    private final String fromName;

    public BrevoEmailSenderAdapter(@Value("${app.mail.brevo.api-key}") String apiKey,
                                   @Value("${app.mail.from}") String fromEmail,
                                   @Value("${app.mail.from-name:Lite Thinking}") String fromName) {
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.restClient = RestClient.builder()
                .baseUrl(BREVO_ENDPOINT)
                .defaultHeader("api-key", apiKey)
                .defaultHeader("accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public void enviarConAdjunto(String destinatario, String asunto, String cuerpo,
                                 byte[] adjunto, String nombreAdjunto) {
        Map<String, Object> payload = Map.of(
                "sender", Map.of("email", fromEmail, "name", fromName),
                "to", List.of(Map.of("email", destinatario)),
                "subject", asunto,
                "textContent", cuerpo,
                "attachment", List.of(Map.of(
                        "content", Base64.getEncoder().encodeToString(adjunto),
                        "name", nombreAdjunto))
        );

        try {
            restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new IllegalStateException("Error enviando el correo a " + destinatario, ex);
        }
    }
}