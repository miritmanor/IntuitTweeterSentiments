package org.intuit.trendingdata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class PersistRawAPIResponse {



    String folder;
    private static final Logger logger = LogManager.getLogger(PersistRawAPIResponse.class);
    PersistRawAPIResponse () {
        folder = System.getenv("STORAGE_FOLDER");
    }

    public void persist(String lastTweetID,String tweetsData) {
        String filename = folder + "/" + lastTweetID;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(tweetsData);
            writer.flush();
        } catch (IOException e) {
            logger.error("Failed to write to file: " + filename + ", "+e.getMessage());
        }
    }
}
