package org.intuit.sentiments;

import org.springframework.stereotype.Component;

import java.util.Random;


/***
 * Analyze sentiments - trivial implementation - random, for testing
 */

@Component
public class MockSentimentAnalyzer implements SentimentAnalyzer {

    private Random random;

    MockSentimentAnalyzer() {
        random=new Random();
    }
    @Override
    public Sentiment getSentiment(String text) {
        int index = random.nextInt(Sentiment.values().length);
        return Sentiment.values()[index];
    }
}
