package com.role.api.repository;

import com.role.api.model.Presenca;
import com.role.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PresencaRepository extends JpaRepository<Presenca, Long> {
    boolean existsByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);
    Optional<Presenca> findByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);
    List<Presenca> findByUsuario(Usuario usuario);
}
