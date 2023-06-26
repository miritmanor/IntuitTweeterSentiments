package org.intuit.sentiments;

import org.springframework.stereotype.Component;

import java.util.Random;


// moving forward the plan is for it to be a separate application, listening for queue messages

/***
 * Analyze sentiments
 * Trivial implementation
 * In practice should use at least an existing language model, and preferably be trained based on relevant data
 * Currenly returns sentiment - a single value. In practice will usually specify % for each sentiment (positive, negative, neutral)
 *  and then need some threshold that determines the resuling sentiment
 */

@Component
public class TrivialSentimentAnalyzerAnalyzer implements SentimentAnalyzer {

    private Random random;

    TrivialSentimentAnalyzerAnalyzer() {
        random=new Random();
    }
    @Override
    public Sentiment getSentiment(String text) {
        int index = random.nextInt(Sentiment.values().length);
        return Sentiment.values()[index];
    }
}
