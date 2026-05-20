package com.role.api.repository;

import com.role.api.model.NotificacaoProcessamento;
import org.springframework.data.jpa.repository.JpaRepository;

// findAll(Sort) já é herdado do JpaRepository — não precisa redeclarar
public interface NotificacaoProcessamentoRepository extends JpaRepository<NotificacaoProcessamento, Long> {
}
