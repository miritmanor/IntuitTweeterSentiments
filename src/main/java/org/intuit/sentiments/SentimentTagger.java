package org.intuit.sentiments;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.intuit.persisttweets.TweetsRepository;
import org.intuit.persisttweets.Tweet;
import org.intuit.messagebroker.TweetEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/***
 * Registered to receive events of raw tweets for handling
 * Each event is expected to be a single tweet in JSON format
 * Alternatives to consider
 *  - each event could be a list of multiple tweets, which can be more efficient in terms of number of messages and service invocations
 *      - batch size should be small enough to allow distribution of events between multiple services
 *
 * For each tweet, get its sentiment and persist.
 * Future:  send to a message bus for further handling
 *
 */
@Component
public class SentimentTagger {

    SentimentAnalyzer sentimentAnalyzer =null;
    ObjectMapper jsonObjectMapper=null;
    TweetsRepository tweetsRepository=null;
    private static final Logger logger = LogManager.getLogger(SentimentTagger.class);

    SentimentTagger(SentimentAnalyzer sentimentAnalyzer, TweetsRepository tweetsRepository) {

        this.sentimentAnalyzer = sentimentAnalyzer;
        this.jsonObjectMapper = new ObjectMapper();
        this.tweetsRepository = tweetsRepository;
    }

    private void persist(TweetEvent tweetEvent, Sentiment tag) {
        Tweet tweet = new Tweet();
        tweet.setTweetID(tweetEvent.getId());
        tweet.setText(tweetEvent.getText());
        tweet.setSentiment(tag);
        tweet.setTimestamp(LocalDateTime.now());

        try {
            tweetsRepository.save(tweet);
        } catch (Exception e) {
            logger.error("Failed to persist event: "+ e);
        }
    }
    public void receiveMessage(String message) {
        logger.info("Received event: " + message);

        TweetEvent tweetEvent;
        try {
            tweetEvent = jsonObjectMapper.readValue(message, TweetEvent.class);
        } catch (Exception e) {
            logger.error("Failed to deserialize event");
            return;
        }
        Sentiment tag = this.sentimentAnalyzer.getSentiment(tweetEvent.getText());
        if (tag != null) {
            logger.info("Sentiment: " + tag);
            persist(tweetEvent, tag);
        } else {
            logger.error("Failed to get sentiment for event, skipping: "+message);
        }

    }

}
