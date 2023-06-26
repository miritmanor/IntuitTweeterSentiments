package org.intuit.trendingdata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.intuit.messagebroker.TweetsQueue;
import org.intuit.messagebroker.TweetsTextQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
@Primary
public class TwitterAPI extends TrendDataAcquisition {
    private String bearerToken=null;
    private HttpClient httpClient=null;
    private final String URI;

    @Autowired
    private LastTweet lastTweet;



    private static final Logger logger = LogManager.getLogger(TwitterAPI.class);

    // Using search/recent API

    TwitterAPI(TweetsQueue tweetsTextQueue) {
        super(tweetsTextQueue);

        bearerToken = System.getenv("BEARER_TOKEN");
        URI = System.getenv("TWITTER_API_URI"); //"https://api.twitter.com/2/tweets/search/recent";

        try {
            httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();


        } catch (Exception e) {
            logger.error("Something went wrong: "+e.getMessage());
        }
    }

    private HttpGet buildHttpGetRequest() {
        HttpGet httpGet;

        // minimal implementation, ir order to not exhaust the API
        // actually should set maxTweets to 100 (the maximum) and get all the available ones (groups of 10)
        // then proceed to see if there is more, until no further tweets available.

        // todo get search string and maxtweets from environment
        String searchString="(Intuit OR TurboTax OR QuickBooks OR Mailchimp) lang:en ";
        String maxTweets="10";

        try {
            URIBuilder uriBuilder = new URIBuilder(URI);
            ArrayList<NameValuePair> queryParameters;

            queryParameters = new ArrayList<>();
            queryParameters.add(new BasicNameValuePair("query", searchString));
            queryParameters.add(new BasicNameValuePair("max_results", maxTweets));
            if (lastTweet.getLastTweet() != null) {
                logger.info("Starting from tweet "+lastTweet.getLastTweet());
                queryParameters.add(new BasicNameValuePair("since_id", lastTweet.getLastTweet()));
            }
            uriBuilder.addParameters(queryParameters);

            httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Authorization", String.format("Bearer %s", bearerToken));
            httpGet.setHeader("Content-Type", "application/json");
            return httpGet;
        } catch (Exception e) {
            logger.error("Something went wrong: "+e.getMessage());
            return null;
        }
    }

    /***
     * Get a batch of tweets from Twitter API
     *
     * Same basic query but always add the id of the last read tweet, so that we get tweets from then on
     * After receiving the tweets list:
     *  - persist as raw data to a data store, for possible later usage and analysis
     *  - go over the received list of tweets, for each one of them
     *      - send the tweet to a message bus/message broker for further handling
     *      - update the id of last tweet to this tweet id, persist it
     *  - recovery from errors
     *    - when starting up, get the last read tweet and start querying from there (alternatively use a timestamp)
     *    -
     */

    public String getNext() {
        String searchResponse;

        try {
            HttpResponse response = httpClient.execute(buildHttpGetRequest());
            logger.info(response);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                searchResponse = EntityUtils.toString(entity, "UTF-8");
                try {
                    handleTweetsGroup(searchResponse.toString());
                } catch (Exception e) {
                    logger.error("Failed to handle tweets - "+e.getMessage());
                }
                return searchResponse;
            }
            return "";
        } catch (Exception e) {
            logger.error("Error while getting tweets: "+e.getMessage());
            return("");
        }



    }

}
