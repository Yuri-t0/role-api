package com.role.api.messaging;

import com.role.api.dto.messaging.PresencaConfirmadaMessage;
import com.role.api.service.NotificacaoProcessamentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresencaConsumer {

    private final NotificacaoProcessamentoService notificacaoProcessamentoService;

    @RabbitListener(queues = "${role.messaging.queue}")
    public void consumir(PresencaConfirmadaMessage message) {
        log.info("Mensagem recebida da fila: usuário={}, eventoId={}",
                message.getUsuarioEmail(), message.getEventoId());
        notificacaoProcessamentoService.processarConfirmacao(message);
    }
}
