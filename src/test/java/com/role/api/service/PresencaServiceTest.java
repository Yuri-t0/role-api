package com.role.api.service;

import com.role.api.messaging.PresencaProducer;
import com.role.api.model.Evento;
import com.role.api.model.Presenca;
import com.role.api.model.Usuario;
import com.role.api.repository.PresencaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresencaServiceTest {

    @Mock private PresencaRepository presencaRepository;
    @Mock private EventoService eventoService;
    @Mock private UsuarioService usuarioService;
    @Mock private PresencaProducer presencaProducer;

    @InjectMocks private PresencaService presencaService;

    private Usuario usuario;
    private Evento evento;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@role.com");
        usuario.setNome("Teste");
        usuario.setSenha("senha");

        evento = new Evento();
        evento.setId(10L);
        evento.setTitulo("Evento Teste");
        evento.setVagasDisponiveis(5);
        evento.setVagasTotais(10);
    }

    @Test
    void confirmar_deveLancarExcecao_quandoPresencaJaExiste() {
        when(usuarioService.buscarPorEmail("teste@role.com")).thenReturn(usuario);
        when(eventoService.buscarPorId(10L)).thenReturn(evento);
        when(presencaRepository.existsByUsuarioIdAndEventoId(1L, 10L)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> presencaService.confirmar(10L, "teste@role.com"));

        verify(presencaRepository, never()).save(any());
    }

    @Test
    void cancelar_deveLancarExcecao_quandoPresencaNaoExiste() {
        when(usuarioService.buscarPorEmail("teste@role.com")).thenReturn(usuario);
        when(presencaRepository.findByUsuarioIdAndEventoId(1L, 10L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> presencaService.cancelar(10L, "teste@role.com"));
    }

    @Test
    void cancelar_deveDevolverVaga_quandoPresencaExiste() {
        Presenca presenca = new Presenca();
        presenca.setUsuario(usuario);
        presenca.setEvento(evento);

        when(usuarioService.buscarPorEmail("teste@role.com")).thenReturn(usuario);
        when(presencaRepository.findByUsuarioIdAndEventoId(1L, 10L)).thenReturn(Optional.of(presenca));
        when(eventoService.buscarPorId(10L)).thenReturn(evento);

        presencaService.cancelar(10L, "teste@role.com");

        verify(presencaRepository).delete(presenca);
        verify(eventoService).salvarEvento(argThat(e -> e.getVagasDisponiveis() == 6));
    }

    @Test
    void jaConfirmou_deveRetornarTrue_quandoPresencaExiste() {
        when(presencaRepository.existsByUsuarioIdAndEventoId(1L, 10L)).thenReturn(true);

        assertTrue(presencaService.jaConfirmou(10L, 1L));
    }
}
