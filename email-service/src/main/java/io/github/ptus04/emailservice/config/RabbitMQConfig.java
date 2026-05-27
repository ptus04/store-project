package io.github.ptus04.emailservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String INVOICE_QUEUE = "email.send-invoice";
    public static final String INVOICE_EXCHANGE = "invoices.topic";
    public static final String INVOICE_CREATED_ROUTING_KEY = "invoice.created";

    @Bean
    public Queue invoiceQueue() {
        return QueueBuilder.durable(INVOICE_QUEUE).build();
    }

    @Bean
    public TopicExchange invoiceExchange() {
        return ExchangeBuilder.topicExchange(INVOICE_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding bindingInvoiceCreated(Queue invoiceQueue, TopicExchange invoiceExchange) {
        return BindingBuilder.bind(invoiceQueue).to(invoiceExchange).with(INVOICE_CREATED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
