package com.role.api.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ─── Dead Letter Queue (DLQ) ───────────────────────────────────────────────

    // FIX #2: DLQ implementada de fato (antes só existia no comentário)
    @Bean
    public DirectExchange presencaDlqExchange(@Value("${role.messaging.exchange}") String exchangeName) {
        return new DirectExchange(exchangeName + ".dlq");
    }

    @Bean
    public Queue presencaDlq(@Value("${role.messaging.queue}") String queueName) {
        return new Queue(queueName + ".dlq", true);
    }

    @Bean
    public Binding presencaDlqBinding(
            @org.springframework.beans.factory.annotation.Qualifier("presencaDlq") Queue dlq,
            @org.springframework.beans.factory.annotation.Qualifier("presencaDlqExchange") DirectExchange dlqExchange,
            @Value("${role.messaging.routing-key}") String routingKey) {
        return BindingBuilder.bind(dlq).to(dlqExchange).with(routingKey);
    }

    // ─── Fila principal (aponta para DLQ em caso de falha) ────────────────────

    @Bean
    public Queue presencaQueue(
            @Value("${role.messaging.queue}") String queueName,
            @Value("${role.messaging.exchange}") String exchangeName) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", exchangeName + ".dlq")
                .withArgument("x-dead-letter-routing-key", "presenca.confirmada")
                .build();
    }

    @Bean
    public DirectExchange presencaExchange(@Value("${role.messaging.exchange}") String exchangeName) {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding presencaBinding(
            @org.springframework.beans.factory.annotation.Qualifier("presencaQueue") Queue presencaQueue,
            @org.springframework.beans.factory.annotation.Qualifier("presencaExchange") DirectExchange presencaExchange,
            @Value("${role.messaging.routing-key}") String routingKey) {
        return BindingBuilder.bind(presencaQueue).to(presencaExchange).with(routingKey);
    }

    // ─── Conversor e Template ──────────────────────────────────────────────────

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
