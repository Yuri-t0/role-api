package com.role.api.service;

import com.role.api.dto.messaging.PresencaConfirmadaMessage;
import com.role.api.messaging.PresencaProducer;
import com.role.api.model.Evento;
import com.role.api.model.Presenca;
import com.role.api.model.Usuario;
import com.role.api.repository.PresencaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PresencaService {

    private final PresencaRepository presencaRepository;
    private final EventoService eventoService;
    private final UsuarioService usuarioService;
    private final PresencaProducer presencaProducer;

    // FIX #7: mensagem enviada somente após commit da transação
    @Transactional
    public Presenca confirmar(Long eventoId, String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        Evento evento = eventoService.buscarPorId(eventoId);

        if (presencaRepository.existsByUsuarioIdAndEventoId(usuario.getId(), eventoId)) {
            throw new IllegalStateException("Presença já confirmada neste evento.");
        }

        eventoService.decrementarVaga(evento);

        Presenca presenca = new Presenca();
        presenca.setUsuario(usuario);
        presenca.setEvento(evento);
        presenca.setConfirmadoEm(LocalDateTime.now());
        presencaRepository.save(presenca);

        PresencaConfirmadaMessage msg =
                new PresencaConfirmadaMessage(eventoId, emailUsuario, presenca.getConfirmadoEm());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                presencaProducer.enviarConfirmacao(msg);
            }
        });

        return presenca;
    }

    // FIX #1: vaga devolvida ao banco corretamente
    @Transactional
    public void cancelar(Long eventoId, String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        Presenca presenca = presencaRepository.findByUsuarioIdAndEventoId(usuario.getId(), eventoId)
                .orElseThrow(() -> new IllegalStateException("Presença não encontrada."));

        presencaRepository.delete(presenca);

        Evento evento = eventoService.buscarPorId(eventoId);
        evento.setVagasDisponiveis(evento.getVagasDisponiveis() + 1);
        eventoService.salvarEvento(evento);
    }

    public List<Presenca> listarPorUsuario(String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        return presencaRepository.findByUsuario(usuario);
    }

    public boolean jaConfirmou(Long eventoId, Long usuarioId) {
        return presencaRepository.existsByUsuarioIdAndEventoId(usuarioId, eventoId);
    }

    // FIX #4: exposto para AdminController não precisar injetar repository
    public long contarTotal() {
        return presencaRepository.count();
    }
}
