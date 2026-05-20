package com.role.api.repository;

import com.role.api.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    // FIX #5: query atômica para evitar race condition no decremento de vagas
    @Modifying
    @Query("UPDATE Evento e SET e.vagasDisponiveis = e.vagasDisponiveis - 1 " +
           "WHERE e.id = :id AND e.vagasDisponiveis > 0")
    int decrementarVagaAtomico(@Param("id") Long id);
}
