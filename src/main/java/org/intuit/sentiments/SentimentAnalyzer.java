package org.intuit.sentiments;

import org.intuit.sentiments.Sentiment;

public interface SentimentAnalyzer {
    public Sentiment getSentiment(String text);
}
