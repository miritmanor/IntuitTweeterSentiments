package org.intuit.persist;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.intuit.sentiments.Sentiment;

import java.time.LocalDateTime;

@Entity
@Table(name = "tweets")
@Setter
@Getter
public class Tweet {
    //@Id
   // @Column
    //@GeneratedValue (strategy = GenerationType.IDENTITY)
    //private Long id;
    @Id
    @Column
    private String tweetID;
    @Column(columnDefinition = "TEXT")
    private String text;
    @Column
    @Enumerated(EnumType.STRING)
    private Sentiment sentiment;
    @Column
    private LocalDateTime timestamp;
}
