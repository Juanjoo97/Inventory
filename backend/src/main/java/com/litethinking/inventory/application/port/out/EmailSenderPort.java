package com.litethinking.inventory.application.port.out;

/** Puerto de salida para el envio de correos. */
public interface EmailSenderPort {
    void enviarConAdjunto(String destinatario, String asunto, String cuerpo,
                          byte[] adjunto, String nombreAdjunto);
}
