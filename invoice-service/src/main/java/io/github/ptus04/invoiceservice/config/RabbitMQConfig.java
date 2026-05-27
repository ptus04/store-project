package io.github.ptus04.invoiceservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
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
    public Binding bindingOrderPaid(Queue invoiceQueue, TopicExchange invoiceExchange) {
        return BindingBuilder.bind(invoiceQueue).to(invoiceExchange).with(ORDER_PAID_ROUTING_KEY);
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
