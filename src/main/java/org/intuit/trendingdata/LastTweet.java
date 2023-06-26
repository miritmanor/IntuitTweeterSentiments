package org.intuit.trendingdata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class LastTweet {
    private String lastTweet;
    private String filename;
    private String folder;

    private static final Logger logger = LogManager.getLogger(LastTweet.class);

    LastTweet() {
        BufferedReader reader;
        folder = System.getenv("STORAGE_FOLDER");
        filename = folder + "/" + "lastTweet";      // todo get from environment
        try {
            FileReader fileReader = new FileReader(filename);
            reader = new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                this.lastTweet=line;
                logger.info("Getting last tweet, setting to "+lastTweet);
            }

        } catch (Exception e) {
            logger.error("Failed to open file for persisting last tweet " + e.getMessage());
            reader =null;
            lastTweet = null;
        }
    }

    public void setLastTweet(String lastTweet) {
        this.lastTweet=lastTweet;
        try {
            FileWriter fileWriter = new FileWriter(filename, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(lastTweet);
            writer.flush();
            logger.info("Wrote last tweet to file "+lastTweet);
        } catch (IOException e) {
            logger.error("Failed to write to file: " + filename + ", "+e.getMessage());
        }
    }

    public String getLastTweet() {
        return this.lastTweet;
    }

}
