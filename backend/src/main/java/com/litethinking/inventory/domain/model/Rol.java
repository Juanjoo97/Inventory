package com.litethinking.inventory.domain.model;

/**
 * Roles de usuario soportados por la aplicacion.
 * ADMIN   -> CRUD de empresas, gestion de productos e inventario.
 * EXTERNO -> visualizacion de empresas (solo lectura).
 */
public enum Rol {
    ADMIN,
    EXTERNO
}
