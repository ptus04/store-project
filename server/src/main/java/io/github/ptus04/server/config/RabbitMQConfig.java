package io.github.ptus04.server.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_CREATE_INVOICE = "invoice.create-invoice";
    public static final String QUEUE_SEND_INVOICE = "email.send-invoice";
    public static final String QUEUE_SEND_OTP = "sms.send-otp";

    public static final String EXCHANGE_INVOICES = "invoices.topic";
    public static final String EXCHANGE_ORDERS = "orders.topic";
    public static final String EXCHANGE_SMS = "sms.topic";

    public static final String KEY_INVOICE_CREATED = "invoice.created";
    public static final String KEY_ORDER_PAID = "order.paid";
    public static final String KEY_SMS_OTP_REQUESTED = "sms.otp-requested";

    @Bean
    public Declarables queues() {
        return new Declarables(
                QueueBuilder.durable(QUEUE_CREATE_INVOICE).build(),
                QueueBuilder.durable(QUEUE_SEND_INVOICE).build(),
                QueueBuilder.durable(QUEUE_SEND_OTP).build()
        );
    }

    @Bean
    public Declarables exchanges() {
        return new Declarables(
                ExchangeBuilder.topicExchange(EXCHANGE_ORDERS).durable(true).build(),
                ExchangeBuilder.topicExchange(EXCHANGE_INVOICES).durable(true).build(),
                ExchangeBuilder.topicExchange(EXCHANGE_SMS).durable(true).build()
        );
    }

    @Bean
    public Declarables amqpBindings() {
        return new Declarables(
                BindingBuilder.bind(new Queue(QUEUE_CREATE_INVOICE)).to(new TopicExchange(EXCHANGE_ORDERS)).with(KEY_ORDER_PAID),
                BindingBuilder.bind(new Queue(QUEUE_SEND_INVOICE)).to(new TopicExchange(EXCHANGE_INVOICES)).with(KEY_INVOICE_CREATED),
                BindingBuilder.bind(new Queue(QUEUE_SEND_OTP)).to(new TopicExchange(EXCHANGE_SMS)).with(KEY_SMS_OTP_REQUESTED)
        );
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
