package com.role.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacao_processamento")
@Getter
@Setter
public class NotificacaoProcessamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evento_id", nullable = false)
    private Long eventoId;

    @Column(name = "usuario_email", nullable = false, length = 150)
    private String usuarioEmail;

    @Column(name = "evento_titulo", nullable = false, length = 200)
    private String eventoTitulo;

    @Column(name = "local_nome", nullable = false, length = 150)
    private String localNome;

    @Column(name = "vagas_restantes", nullable = false)
    private Integer vagasRestantes;

    @Column(name = "processado_em", nullable = false)
    private LocalDateTime processadoEm;
}
