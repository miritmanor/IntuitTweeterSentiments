package org.intuit.trendingdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.intuit.messagebroker.TweetsQueue;
import org.intuit.messagebroker.TweetEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class TrendDataAcquisition {
    private final ObjectMapper mapper;
    private final TweetsQueue tweetsTextQueue;

    @Autowired
    PersistRawAPIResponse persistResponse;
    @Autowired
    private LastTweet lastTweet;

    public TrendDataAcquisition (TweetsQueue tweetsTextQueue) {
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.tweetsTextQueue = tweetsTextQueue;
    }

    public abstract String getNext();

    protected void handleTweetsGroup(String tweets) throws JsonProcessingException {

        TweetsGroup tweetsGroup = mapper.readValue(tweets, TweetsGroup.class);

        if (tweetsGroup.getMeta().getResult_count() > 0) {
            // persist the response - for future further analysis
            persistResponse.persist(tweetsGroup.getMeta().getNewest_id(), tweets);

            // go over list and send each tweet for processing

            List<TweetsGroup.TweetData> data = tweetsGroup.getData();
            for (TweetsGroup.TweetData tweet : data) {
                // create event
                TweetEvent tweetEvent = new TweetEvent();
                tweetEvent.setId(tweet.getId());
                tweetEvent.setText(tweet.getText());

                // now serialize tweetEvent to JSON and send to message broker
                String jsonEvent = mapper.writeValueAsString(tweetEvent);
                tweetsTextQueue.sendEvent(jsonEvent);
            }

            // now update last tweet, so that next time we start with the next one
            lastTweet.setLastTweet(tweetsGroup.getMeta().getNewest_id());
        }

    }
}
