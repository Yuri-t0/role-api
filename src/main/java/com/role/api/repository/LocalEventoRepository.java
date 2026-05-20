package com.role.api.repository;

import com.role.api.model.LocalEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalEventoRepository extends JpaRepository<LocalEvento, Long> {
}
