package com.role.api.service;

import com.role.api.model.Evento;
import com.role.api.repository.EventoRepository;
import com.role.api.repository.LocalEventoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock private EventoRepository eventoRepository;
    @Mock private UsuarioService usuarioService;
    @Mock private LocalEventoRepository localEventoRepository;

    @InjectMocks private EventoService eventoService;

    @Test
    void buscarPorId_deveLancarExcecao_quandoNaoEncontrado() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> eventoService.buscarPorId(99L));
    }

    @Test
    void decrementarVaga_deveLancarExcecao_quandoSemVagas() {
        when(eventoRepository.decrementarVagaAtomico(10L)).thenReturn(0);

        Evento evento = new Evento();
        evento.setId(10L);
        evento.setVagasDisponiveis(0);

        assertThrows(IllegalStateException.class,
                () -> eventoService.decrementarVaga(evento));
    }

    @Test
    void decrementarVaga_deveExecutarSemExcecao_quandoHaVagas() {
        when(eventoRepository.decrementarVagaAtomico(10L)).thenReturn(1);

        Evento evento = new Evento();
        evento.setId(10L);
        evento.setVagasDisponiveis(3);

        assertDoesNotThrow(() -> eventoService.decrementarVaga(evento));
    }
}
