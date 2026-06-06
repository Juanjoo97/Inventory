package com.litethinking.inventory.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Linea de detalle de una orden. Resuelve la relacion N:M entre Orden y Producto,
 * almacenando cantidad y precio unitario al momento de la compra.
 */
@Entity
@Table(name = "orden_detalle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal precioUnitario;
}
