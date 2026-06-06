package com.litethinking.inventory.infrastructure.persistence;

import com.litethinking.inventory.domain.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNombre(String nombre);
}
