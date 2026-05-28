package io.github.ptus04.server.invoice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InvoiceRabbitMQConfig {
    public static final String CREATE_INVOICE_QUEUE = "invoice.create-invoice";
    public static final String ORDER_EXCHANGE = "orders.topic";
    public static final String ORDER_PAID_ROUTING_KEY = "order.paid";

    public static final String INVOICE_EXCHANGE = "invoices.topic";
    public static final String INVOICE_CREATED_ROUTING_KEY = "invoice.created";

    @Bean
    public Queue createInvoiceQueue() {
        return QueueBuilder.durable(CREATE_INVOICE_QUEUE).build();
    }

    @Bean
    public TopicExchange ordersExchange() {
        return ExchangeBuilder.topicExchange(ORDER_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding bindingOrderPaid(Queue createInvoiceQueue, TopicExchange invoiceExchange) {
        return BindingBuilder.bind(createInvoiceQueue).to(invoiceExchange).with(ORDER_PAID_ROUTING_KEY);
    }
}
