package com.role.api.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoResumoResponse {
    private Long id;
    private String titulo;
    private String localNome;
    private Integer vagasDisponiveis;
}
