package com.litethinking.inventory.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Precio de un producto en una moneda especifica (ISO 4217, ej: COP, USD, EUR).
 */
@Entity
@Table(name = "producto_precio",
        uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "moneda"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoPrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false, length = 3)
    private String moneda;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;
}
