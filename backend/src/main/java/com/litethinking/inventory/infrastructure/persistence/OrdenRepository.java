package com.litethinking.inventory.infrastructure.persistence;

import com.litethinking.inventory.domain.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
}
