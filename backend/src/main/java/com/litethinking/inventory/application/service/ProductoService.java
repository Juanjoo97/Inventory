package com.litethinking.inventory.application.service;

import com.litethinking.inventory.application.dto.PrecioDto;
import com.litethinking.inventory.application.dto.ProductoRequest;
import com.litethinking.inventory.application.dto.ProductoResponse;
import com.litethinking.inventory.application.mapper.ProductoMapper;
import com.litethinking.inventory.application.port.out.CategoriaRepositoryPort;
import com.litethinking.inventory.application.port.out.EmpresaRepositoryPort;
import com.litethinking.inventory.application.port.out.ProductoRepositoryPort;
import com.litethinking.inventory.domain.exception.DuplicateResourceException;
import com.litethinking.inventory.domain.exception.ResourceNotFoundException;
import com.litethinking.inventory.domain.model.Categoria;
import com.litethinking.inventory.domain.model.Empresa;
import com.litethinking.inventory.domain.model.Producto;
import com.litethinking.inventory.domain.model.ProductoPrecio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepositoryPort productoRepository;
    private final EmpresaRepositoryPort empresaRepository;
    private final CategoriaRepositoryPort categoriaRepository;

    public ProductoService(ProductoRepositoryPort productoRepository,
                           EmpresaRepositoryPort empresaRepository,
                           CategoriaRepositoryPort categoriaRepository) {
        this.productoRepository = productoRepository;
        this.empresaRepository = empresaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(String empresaNit) {
        List<Producto> productos = StringUtils.hasText(empresaNit)
                ? productoRepository.findByEmpresaNit(empresaNit)
                : productoRepository.findAll();
        return productos.stream().map(ProductoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return ProductoMapper.toResponse(buscarOError(id));
    }

    public ProductoResponse crear(ProductoRequest req) {
        if (productoRepository.existsByCodigo(req.codigo())) {
            throw new DuplicateResourceException("Ya existe un producto con codigo " + req.codigo());
        }
        Empresa empresa = empresaRepository.findByNit(req.empresaNit())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la empresa con NIT " + req.empresaNit()));

        Producto producto = Producto.builder()
                .codigo(req.codigo())
                .nombre(req.nombre())
                .caracteristicas(req.caracteristicas())
                .empresa(empresa)
                .build();

        aplicarPrecios(producto, req.precios());
        producto.setCategorias(resolverCategorias(req.categoriaIds()));

        return ProductoMapper.toResponse(productoRepository.save(producto));
    }

    public ProductoResponse actualizar(Long id, ProductoRequest req) {
        Producto producto = buscarOError(id);

        if (!producto.getCodigo().equals(req.codigo()) && productoRepository.existsByCodigo(req.codigo())) {
            throw new DuplicateResourceException("Ya existe un producto con codigo " + req.codigo());
        }
        Empresa empresa = empresaRepository.findByNit(req.empresaNit())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la empresa con NIT " + req.empresaNit()));

        producto.setCodigo(req.codigo());
        producto.setNombre(req.nombre());
        producto.setCaracteristicas(req.caracteristicas());
        producto.setEmpresa(empresa);
        aplicarPrecios(producto, req.precios());
        producto.setCategorias(resolverCategorias(req.categoriaIds()));

        return ProductoMapper.toResponse(productoRepository.save(producto));
    }

    public void eliminar(Long id) {
        Producto producto = buscarOError(id);
        productoRepository.deleteById(producto.getId());
    }


    private void aplicarPrecios(Producto producto, List<PrecioDto> preciosDto) {

        Map<String, ProductoPrecio> actuales = producto.getPrecios()
                .stream()
                .collect(Collectors.toMap(
                        p -> p.getMoneda().toUpperCase(),
                        p -> p
                ));

        for (PrecioDto dto : preciosDto) {
            ProductoPrecio existente = actuales.get(dto.moneda().toUpperCase());

            if (existente != null) {
                existente.setValor(dto.valor());
            } else {
                producto.addPrecio(
                        ProductoPrecio.builder()
                                .moneda(dto.moneda().toUpperCase())
                                .valor(dto.valor())
                                .build()
                );
            }
        }
    }

    private Set<Categoria> resolverCategorias(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        List<Categoria> encontradas = categoriaRepository.findAllByIds(ids);
        if (encontradas.size() != new HashSet<>(ids).size()) {
            throw new ResourceNotFoundException("Una o mas categorias no existen");
        }
        return new HashSet<>(encontradas);
    }

    private Producto buscarOError(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el producto con id " + id));
    }
}
