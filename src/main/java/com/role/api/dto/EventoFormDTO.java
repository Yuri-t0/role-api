package com.role.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventoFormDTO {

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotNull(message = "A data de início é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime inicio;

    @NotNull(message = "A data de fim é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fim;

    @NotNull(message = "A quantidade de vagas é obrigatória")
    @Min(value = 1, message = "O evento deve ter ao menos 1 vaga")
    private Integer vagasTotais;

    @NotNull(message = "O organizador é obrigatório")
    private Long organizadorId;

    @NotNull(message = "O local é obrigatório")
    private Long localId;
}
