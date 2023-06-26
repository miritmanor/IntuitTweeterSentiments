package org.intuit.messagebroker;

import org.intuit.sentiments.SentimentTagger;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

public interface TweetsQueue {

    void sendEvent(String message );
    MessageListenerAdapter listenerAdapter(SentimentTagger service);
}
