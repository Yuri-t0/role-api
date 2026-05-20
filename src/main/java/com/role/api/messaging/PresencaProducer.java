package com.role.api.messaging;

import com.role.api.dto.messaging.PresencaConfirmadaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresencaProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${role.messaging.exchange}")
    private String exchange;

    @Value("${role.messaging.routing-key}")
    private String routingKey;

    public void enviarConfirmacao(PresencaConfirmadaMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        log.info("Mensagem enviada para fila: usuário={}, eventoId={}",
                message.getUsuarioEmail(), message.getEventoId());
    }
}
