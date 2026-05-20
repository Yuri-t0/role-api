package com.role.api.controller.api;

import com.role.api.dto.response.EventoResumoResponse;
import com.role.api.model.Evento;
import com.role.api.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interno/eventos")
@RequiredArgsConstructor
public class EventoInternoController {

    private final EventoService eventoService;

    @GetMapping("/{id}/resumo")
    public EventoResumoResponse buscarResumo(@PathVariable Long id) {
        Evento evento = eventoService.buscarPorId(id);

        EventoResumoResponse response = new EventoResumoResponse();
        response.setId(evento.getId());
        response.setTitulo(evento.getTitulo());
        response.setLocalNome(evento.getLocal().getNome());
        response.setVagasDisponiveis(evento.getVagasDisponiveis());

        return response;
    }
}
