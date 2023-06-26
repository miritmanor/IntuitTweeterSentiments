package org.intuit.trendingdata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.intuit.messagebroker.TweetsQueue;
import org.intuit.messagebroker.TweetsTextQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Scanner;


@Component

public class MockData extends TrendDataAcquisition {

    Scanner scanner = null;

    private static final Logger logger = LogManager.getLogger(MockData.class);

    // todo get from environment
    String filePath = "TweeterMockData.txt";

    @Autowired
    MockData(TweetsQueue tweetsTextQueue) {
        super(tweetsTextQueue);
        try {
            File file = new File(filePath);
            scanner = new Scanner(file);
        } catch (Exception e) {
            logger.error("Error opening file "+filePath+" : "+e.getMessage());
            scanner=null;
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
                logger.error("Failed to handle tweets - "+e.getMessage());
            }
            return stringBuilder.toString();

        } else {
            return ("");
        }
    }
}
