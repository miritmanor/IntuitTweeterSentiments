package org.intuit.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.intuit.messagebroker.TweetsTextQueue;
import org.intuit.objects.TweetEvent;
import org.intuit.trendingdata.TweetsGroup;

import java.util.List;

public abstract class TrendDataAcquisition {
    private ObjectMapper mapper;
    private TweetsTextQueue tweetsTextQueue;


    public TrendDataAcquisition (TweetsTextQueue tweetsTextQueue) {
        mapper = new ObjectMapper();
        this.tweetsTextQueue = tweetsTextQueue;
    }

    public abstract String getNext();

    protected void handleTweetsGroup(String tweets) throws JsonProcessingException {

        TweetsGroup tweetsGroup = mapper.readValue(tweets, TweetsGroup.class);

        List<TweetsGroup.TweetData> data = tweetsGroup.getData();
        for (TweetsGroup.TweetData tweet: data) {
            // create event
            TweetEvent tweetEvent = new TweetEvent();
            tweetEvent.setId(tweet.getId());
            tweetEvent.setText(tweet.getText());

            // now serialize tweetEvent to JSON and send to message broker
            String jsonEvent = mapper.writeValueAsString(tweetEvent);

            tweetsTextQueue.sendEvent(jsonEvent.toString());
        }
    }
}
