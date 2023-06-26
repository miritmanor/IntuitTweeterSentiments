package org.intuit.messagebroker;

import lombok.Data;

@Data
public class TweetEvent {
    private String id;
    private String text;
}
