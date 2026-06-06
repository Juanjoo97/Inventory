package com.litethinking.inventory.domain.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Empresa. El NIT actua como llave primaria natural (requisito del reto).
 */
@Entity
@Table(name = "empresa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empresa {

    @Id
    @Column(name = "nit", length = 40)
    private String nit;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 255)
    private String direccion;

    @Column(length = 40)
    private String telefono;
}
