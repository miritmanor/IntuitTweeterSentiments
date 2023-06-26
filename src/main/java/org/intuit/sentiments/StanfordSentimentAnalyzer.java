package org.intuit.sentiments;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Properties;


@Component
@Primary
public class StanfordSentimentAnalyzer implements SentimentAnalyzer {

    StanfordCoreNLP pipeline;
    private static final Logger logger = LogManager.getLogger(StanfordSentimentAnalyzer.class);
    StanfordSentimentAnalyzer() {

        try {
            Properties props = new Properties();
            // edu/stanford/nlp/models/sentiment/sentiment.ser.gz
            props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
            pipeline = new StanfordCoreNLP(props);
        } catch (Exception e) {
            logger.error("failed to initialize stanford lib: "+e);
        }

    }
    @Override
    public Sentiment getSentiment(String text) {

        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        // Get the sentiment score
        CoreMap sentence = document.get(CoreAnnotations.SentencesAnnotation.class).get(0);
        String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

        // Print the sentiment
        logger.info("Sentiment: " + sentiment);

        try {
            Sentiment value = Sentiment.valueOf(sentiment.toLowerCase());
            return value;
        } catch (Exception e) {
            logger.error("Unsupported sentiment: "+sentiment);
            return null;
        }

        /**
        logger.info("text:"+text);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        StringBuilder tokens = new StringBuilder();

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                tokens.append(word).append(" ");
            }
        }
        logger.info(tokens);
        return Sentiment.positive;
         **/
    }
}
