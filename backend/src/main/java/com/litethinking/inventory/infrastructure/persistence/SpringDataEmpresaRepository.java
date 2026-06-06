package com.litethinking.inventory.infrastructure.persistence;

import com.litethinking.inventory.domain.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataEmpresaRepository extends JpaRepository<Empresa, String> {
}
