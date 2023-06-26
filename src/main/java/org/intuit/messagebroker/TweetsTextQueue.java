package org.intuit;

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
public class TweetsTextQueue extends RabbitMQAccess {
    static final String topicExchangeName = "tweetsText";
    static final String queueName = "tweetsText";
    static final String routingKey = "";
    static final String receiveMessagesMethodName ="receiveMessage";

    // todo this is too specific - need an interface here
    //private SentimentAnalyzer receiver;

    public TweetsTextQueue(RabbitTemplate rabbitTemplate, SentimentAnalyzer receiver) {
        super(rabbitTemplate);
        //this.receiver = receiver;
    }

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }
    @Bean
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
    MessageListenerAdapter listenerAdapter(SentimentTagger sentimentTagger) {
        return new MessageListenerAdapter(sentimentTagger, receiveMessagesMethodName);
    }

    public void sendEvent(String message ) {
        rabbitTemplate.convertAndSend(topicExchangeName, routingKey, message);
    }


}
