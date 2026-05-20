package com.role.api.dto.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresencaConfirmadaMessage implements Serializable {
    private Long eventoId;
    private String usuarioEmail;
    private LocalDateTime confirmadoEm;
}
