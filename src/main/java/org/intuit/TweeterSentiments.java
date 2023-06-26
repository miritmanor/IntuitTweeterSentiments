package org.intuit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
@ComponentScan(basePackages = {"org.intuit", "org.intuit.trendingdata","org.intuit.storesentiments"})
@EnableJpaRepositories
@EntityScan
public class TweeterSentiments {

    public static void main(String[] args) throws IOException, URISyntaxException {

        ApplicationContext context = SpringApplication.run(TweeterSentiments.class, args);
        //GetTweets tweets = context.getBean(GetTweets.class);
        //tweets.getTweets();
    }

}