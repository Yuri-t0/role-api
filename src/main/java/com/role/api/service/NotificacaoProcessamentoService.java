package com.role.api.service;

import com.role.api.dto.messaging.PresencaConfirmadaMessage;
import com.role.api.model.Evento;
import com.role.api.model.NotificacaoProcessamento;
import com.role.api.repository.NotificacaoProcessamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// FIX #3: removido EventoClient (chamada HTTP circular para si mesmo)
//         substituído por injeção direta do EventoService
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoProcessamentoService {

    private final NotificacaoProcessamentoRepository notificacaoRepository;
    private final EventoService eventoService; // antes era EventoClient

    public void processarConfirmacao(PresencaConfirmadaMessage message) {
        // FIX #3: exceção relançada para que a DLQ funcione corretamente
        Evento evento = eventoService.buscarPorId(message.getEventoId());

        NotificacaoProcessamento notificacao = new NotificacaoProcessamento();
        notificacao.setEventoId(message.getEventoId());
        notificacao.setUsuarioEmail(message.getUsuarioEmail());
        notificacao.setEventoTitulo(evento.getTitulo());
        notificacao.setLocalNome(evento.getLocal().getNome());
        notificacao.setVagasRestantes(evento.getVagasDisponiveis());
        notificacao.setProcessadoEm(LocalDateTime.now());

        notificacaoRepository.save(notificacao);

        log.info("Presença processada: usuário {} no evento '{}'",
                message.getUsuarioEmail(), evento.getTitulo());
    }

    public List<NotificacaoProcessamento> listarProcessamentos() {
        return notificacaoRepository.findAll(Sort.by(Sort.Direction.DESC, "processadoEm"));
    }
}
