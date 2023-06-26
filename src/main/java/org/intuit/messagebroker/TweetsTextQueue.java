package org.intuit.messagebroker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.intuit.sentiments.SentimentTagger;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class TweetsTextQueue implements TweetsQueue {
    String topicExchangeName = "tweetsText";
    String queueName = "tweetsText";
    String routingKey = "";
    String receiveMessagesMethodName ="receiveMessage";

    RabbitTemplate rabbitTemplate;

    private static final Logger logger = LogManager.getLogger(TweetsTextQueue.class);
    public TweetsTextQueue(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    Queue queue() {
        return new Queue(queueName, false);
    }

    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(SentimentTagger service) {
        return new MessageListenerAdapter(service, receiveMessagesMethodName);
    }

    public void sendEvent(String message ) {
        rabbitTemplate.convertAndSend(topicExchangeName, routingKey, message);
    }


}
