package com.litethinking.inventory.infrastructure.config;

import com.litethinking.inventory.domain.model.*;
import com.litethinking.inventory.infrastructure.persistence.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Carga datos iniciales si la base esta vacia:
 * - Usuarios ADMIN y EXTERNO (contrasena con hash BCrypt).
 * - Categorias, empresas y productos de ejemplo (precios multimoneda).
 * - Un cliente con una orden para ejercitar las relaciones Cliente/Orden/Producto.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final SpringDataUsuarioRepository usuarioRepository;
    private final SpringDataEmpresaRepository empresaRepository;
    private final SpringDataProductoRepository productoRepository;
    private final SpringDataCategoriaRepository categoriaRepository;
    private final ClienteRepository clienteRepository;
    private final OrdenRepository ordenRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(SpringDataUsuarioRepository usuarioRepository,
                      SpringDataEmpresaRepository empresaRepository,
                      SpringDataProductoRepository productoRepository,
                      SpringDataCategoriaRepository categoriaRepository,
                      ClienteRepository clienteRepository,
                      OrdenRepository ordenRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.clienteRepository = clienteRepository;
        this.ordenRepository = ordenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsuarios();
        if (empresaRepository.count() == 0) {
            seedDatosDeNegocio();
        }
    }

    private void seedUsuarios() {
        if (!usuarioRepository.existsByEmail("admin@litethinking.com")) {
            usuarioRepository.save(Usuario.builder()
                    .email("admin@litethinking.com")
                    .password(passwordEncoder.encode("Admin123*"))
                    .rol(Rol.ADMIN)
                    .build());
        }
        if (!usuarioRepository.existsByEmail("externo@litethinking.com")) {
            usuarioRepository.save(Usuario.builder()
                    .email("externo@litethinking.com")
                    .password(passwordEncoder.encode("Externo123*"))
                    .rol(Rol.EXTERNO)
                    .build());
        }
    }

    private void seedDatosDeNegocio() {
        Categoria tecnologia = categoriaRepository.save(Categoria.builder().nombre("Tecnologia").build());
        Categoria hogar = categoriaRepository.save(Categoria.builder().nombre("Hogar").build());
        Categoria oficina = categoriaRepository.save(Categoria.builder().nombre("Oficina").build());

        Empresa acme = empresaRepository.save(Empresa.builder()
                .nit("900123456-7").nombre("ACME S.A.S")
                .direccion("Calle 10 # 20-30, Bogota").telefono("6017001234").build());

        Empresa globex = empresaRepository.save(Empresa.builder()
                .nit("901987654-3").nombre("Globex Ltda")
                .direccion("Carrera 50 # 5-15, Medellin").telefono("6044005678").build());

        Producto laptop = nuevoProducto("P-001", "Laptop Pro 14", "16GB RAM, SSD 512GB", acme,
                Set.of(tecnologia, oficina),
                precio("COP", "4500000"), precio("USD", "1150.00"));

        nuevoProducto("P-002", "Mouse inalambrico", "Bluetooth, recargable", acme,
                Set.of(tecnologia),
                precio("COP", "120000"), precio("USD", "30.00"));

        Producto silla = nuevoProducto("P-003", "Silla ergonomica", "Soporte lumbar ajustable", globex,
                Set.of(hogar, oficina),
                precio("COP", "850000"), precio("USD", "215.00"), precio("EUR", "200.00"));

        // Cliente + orden de ejemplo (ejercita Cliente, Orden, OrdenDetalle)
        Cliente cliente = clienteRepository.save(Cliente.builder()
                .nombre("Juan Perez").email("juan.perez@example.com").build());

        Orden orden = Orden.builder().cliente(cliente).fecha(LocalDateTime.now()).build();
        orden.getDetalles().add(OrdenDetalle.builder()
                .orden(orden).producto(laptop).cantidad(1).precioUnitario(new BigDecimal("4500000")).build());
        orden.getDetalles().add(OrdenDetalle.builder()
                .orden(orden).producto(silla).cantidad(2).precioUnitario(new BigDecimal("850000")).build());
        ordenRepository.save(orden);
    }

    private Producto nuevoProducto(String codigo, String nombre, String caracteristicas,
                                   Empresa empresa, Set<Categoria> categorias, ProductoPrecio... precios) {
        Producto producto = Producto.builder()
                .codigo(codigo).nombre(nombre).caracteristicas(caracteristicas)
                .empresa(empresa).categorias(categorias).build();
        for (ProductoPrecio p : precios) {
            producto.addPrecio(p);
        }
        return productoRepository.save(producto);
    }

    private ProductoPrecio precio(String moneda, String valor) {
        return ProductoPrecio.builder().moneda(moneda).valor(new BigDecimal(valor)).build();
    }
}
