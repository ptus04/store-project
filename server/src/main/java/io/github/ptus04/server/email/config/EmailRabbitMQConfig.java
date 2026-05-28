package io.github.ptus04.server.email.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailRabbitMQConfig {
    public static final String SEND_INVOICE_QUEUE = "email.send-invoice";
    public static final String INVOICE_EXCHANGE = "invoices.topic";
    public static final String INVOICE_CREATED_ROUTING_KEY = "invoice.created";

    @Bean
    public Queue sendInvoiceQueue() {
        return QueueBuilder.durable(SEND_INVOICE_QUEUE).build();
    }

    @Bean
    public TopicExchange invoiceExchange() {
        return ExchangeBuilder.topicExchange(INVOICE_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding bindingInvoiceCreated(Queue sendInvoiceQueue, TopicExchange invoiceExchange) {
        return BindingBuilder.bind(sendInvoiceQueue).to(invoiceExchange).with(INVOICE_CREATED_ROUTING_KEY);
    }
}
