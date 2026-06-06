package com.litethinking.inventory.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Producto perteneciente a una empresa.
 * - Soporta precios en multiples monedas (relacion 1:N con ProductoPrecio).
 * - Puede pertenecer a multiples categorias (relacion N:M con Categoria).
 */
@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 2000)
    private String caracteristicas;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_nit", nullable = false)
    private Empresa empresa;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ProductoPrecio> precios = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "producto_categoria",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @Builder.Default
    private Set<Categoria> categorias = new HashSet<>();

    /** Agrega un precio manteniendo la relacion bidireccional consistente. */
    public void addPrecio(ProductoPrecio precio) {
        precio.setProducto(this);
        this.precios.add(precio);
    }

    /** Elimina todos los precios (orphanRemoval los borra de la BD). */
    public void clearPrecios() {
        this.precios.clear();
    }
}
