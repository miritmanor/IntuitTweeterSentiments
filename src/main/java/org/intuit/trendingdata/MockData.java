package org.intuit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.intuit.interfaces.TrendDataAcquisition;
import org.intuit.interfaces.Sentiment;
import org.intuit.interfaces.TweetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.intuit.trendingdata.TweetsGroup;

import java.io.File;
import java.util.List;
import java.util.Scanner;


@Component
@Primary
public class MockData implements TrendDataAcquisition {

    Scanner scanner = null;
    Sentiment sentiment=null;
    ObjectMapper jsonObjectMapper=null;
    TweetsTextQueue tweetsTextQueue = null;

    // todo get from environment
    String filePath = "TweeterMockData.txt";
    TweetsRepository tweetsRepository=null;
    @Autowired
    MockData(Sentiment sentiment, TweetsRepository tweetsRepository, TweetsTextQueue tweetsTextQueue) {
        this.sentiment = sentiment;
        try {
            File file = new File(filePath);
            scanner = new Scanner(file);
        } catch (Exception e) {
            System.out.println("Error opening file "+filePath+" : "+e.getMessage());
            scanner=null;
        }
        this.tweetsRepository = tweetsRepository;
        this.jsonObjectMapper = new ObjectMapper();
        this.tweetsTextQueue= tweetsTextQueue;
    }

    // todo possibly move to its own class
    private void handleTweetsGroup(String tweets) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        TweetsGroup tweetsGroup = mapper.readValue(tweets, TweetsGroup.class);
        // todo go over tweets, for each one get sentiment and add to the data
        List<TweetsGroup.TweetData> data = tweetsGroup.getData();
        for (TweetsGroup.TweetData tweet: data) {
            System.out.println(tweet.getText());
            //Sentiment.SentimentTag st= sentiment.getSentiment(tweet.getText());
            //System.out.println("Sentiment: "+ st);
            //Tweets tweetRecord = new Tweets();
            //tweetRecord.setTweetID(tweet.getId());
            //tweetRecord.setText(tweet.getText());
            //tweetRecord.setSentiment(st);
            //tweetRecord.setTimestamp(Instant.now());
            //tweetsRepository.save(tweetRecord);

            // create event and write to queue

            TweetEvent tweetEvent = new TweetEvent();
            tweetEvent.setId(tweet.getId());
            tweetEvent.setText(tweet.getText());
            // now serialize tweetEvent to JSON and send to message broker
            String jsonEvent = jsonObjectMapper.writeValueAsString(tweetEvent);

            tweetsTextQueue.sendEvent(jsonEvent.toString());
        }
    }
    public String getNext() {
        if (scanner != null) {
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            try {
                handleTweetsGroup(stringBuilder.toString());
            } catch (Exception e) {
                System.out.println("Failed to handle tweets - "+e.getMessage());
            }
            // todo go over this and reorder - need to supply a handler and handle properly

            return stringBuilder.toString();

        } else {
            return ("");
        }
    }
}
