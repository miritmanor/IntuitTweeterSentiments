package org.intuit.persist;

import org.intuit.sentiments.Sentiment;
import org.intuit.persist.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TweetsRepository extends JpaRepository<Tweet, String> {
    List<Tweet> findBySentiment(Sentiment sentiment);
}
