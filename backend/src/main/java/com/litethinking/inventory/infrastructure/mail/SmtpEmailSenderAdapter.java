package com.litethinking.inventory.infrastructure.mail;

import com.litethinking.inventory.application.port.out.EmailSenderPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Implementacion del puerto de envio de correo usando SMTP (Spring Mail).
 */
@Component
@ConditionalOnProperty(name = "app.mail.provider", havingValue = "smtp", matchIfMissing = true)
public class SmtpEmailSenderAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;
    private final String from;

    public SmtpEmailSenderAdapter(JavaMailSender mailSender,
                                  @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void enviarConAdjunto(String destinatario, String asunto, String cuerpo,
                                 byte[] adjunto, String nombreAdjunto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo);
            helper.addAttachment(nombreAdjunto, new ByteArrayResource(adjunto), "application/pdf");
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Error enviando el correo a " + destinatario, ex);
        }
    }
}