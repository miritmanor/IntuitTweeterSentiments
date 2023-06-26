package trendingdata;

import interfaces.TrendDataAcquisition;
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

import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class TwitterAPI implements TrendDataAcquisition {

    //String bearerToken="AAAAAAAAAAAAAAAAAAAAAA3hfAEAAAAAKeYPU6ntjNTx288mpuHc4hNm6bA%3D5yDS1Ps7aLHms4q2tk91fUTLb4c8ChkxXjFJn0QaeMYUc2kmwO";
    String bearerToken=null;
    HttpClient httpClient=null;
    String URI="https://api.twitter.com/2/tweets/search/recent";
    HttpGet httpGet=null;
    String searchString="intuit";


    TwitterAPI() {

        // TODO get bearer token from env
        bearerToken="AAAAAAAAAAAAAAAAAAAAAN%2FkoAEAAAAAV9TaUI2mx%2BqXoB9UM7aSRusTsDQ%3Dhv4vDVi5dDQjLNru7PpyS2DZGjp32SI3bhtDWZJhOVd0giD4P5";

        // TODO get last tweet so that we start from where we left off

        try {
            httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();

            // TODO get URI from env
            URIBuilder uriBuilder = new URIBuilder(URI);
            ArrayList<NameValuePair> queryParameters;

            // TODO search for last tweet in order to create the correct query, also add handling for missing lont time periods that require extended search
            queryParameters = new ArrayList<>();
            queryParameters.add(new BasicNameValuePair("query", searchString));
            uriBuilder.addParameters(queryParameters);

            httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Authorization", String.format("Bearer %s", bearerToken));
            httpGet.setHeader("Content-Type", "application/json");
        } catch (Exception e) {
            System.out.println("Something went wrong: "+e.getMessage());
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
        String searchResponse = null;

        try {
            HttpResponse response = httpClient.execute(httpGet);
            System.out.println(response);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                searchResponse = EntityUtils.toString(entity, "UTF-8");
            }
            return searchResponse;
        } catch (Exception e) {
            System.out.println("Error while getting tweets: "+e.getMessage());
            return("");
        }



        // TODO persist the response
    }

}
