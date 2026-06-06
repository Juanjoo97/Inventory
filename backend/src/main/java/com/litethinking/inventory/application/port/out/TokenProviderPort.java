package com.litethinking.inventory.application.port.out;

/** Puerto de salida para la generacion de tokens de autenticacion. */
public interface TokenProviderPort {
    String generarToken(String email, String rol);
}
