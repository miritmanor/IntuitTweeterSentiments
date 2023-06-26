package org.intuit.interfaces;

import org.intuit.objects.Sentiment;

public interface SentimentAnalyzer {
    public Sentiment getSentiment(String text);
}
