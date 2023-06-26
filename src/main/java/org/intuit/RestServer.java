package org.intuit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.intuit.trendingdata.TrendDataAcquisition;
import org.intuit.persisttweets.TweetsRepository;
import org.intuit.sentiments.Sentiment;
import org.intuit.persisttweets.Tweet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.Logger;

import java.util.List;

@RestController
public class RestServer {

    private final TrendDataAcquisition getTweets;
    private ObjectMapper mapper;
    private TweetsRepository tweetsRepository;
    private static final Logger logger = LogManager.getLogger(RestServer.class);

    RestServer(TrendDataAcquisition gett, TweetsRepository tweetsRepository) {
        //queue=mq;
        getTweets = gett;
        this.tweetsRepository = tweetsRepository;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    }

    // get all tweets with a specific sentiment
    // sentiment is a parameter, later on more parameters like time and day, will be added

    @GetMapping("/tweets")
    public ResponseEntity<String> getTweets(@RequestParam("sentiment") String sentimentString) {
        Sentiment sentiment;
        try {
            sentiment = Sentiment.valueOf(sentimentString.toLowerCase());
        } catch (Exception e) {
            logger.error("problem with parameter: "+sentimentString+e);
            return new ResponseEntity<>("problem with parameter ", HttpStatus.BAD_REQUEST);
        }
        try {
            List<Tweet> tweets = tweetsRepository.findBySentiment(sentiment);
            String jsonTweets = mapper.writeValueAsString((tweets));
            logger.info(jsonTweets);
            return new ResponseEntity<>(jsonTweets, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("failed to process: "+e);
            return new ResponseEntity<>("Failed to process ",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // invoke the API to get more tweets
    // 'POST' because we are actually adding data, although it's from another source
    @PostMapping("/tweets")
    public ResponseEntity<String> getTweets() {
        logger.info("Getting tweets more tweets from API...");
        try {
            String tweetsString = getTweets.getNext();
            logger.info("tweets string: " + tweetsString);
            return new ResponseEntity<>(tweetsString, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("failed to process: "+e);
            return new ResponseEntity<>("Failed to process ",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
