package org.intuit.persisttweets;

import org.intuit.sentiments.Sentiment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TweetsRepository extends JpaRepository<Tweet, String> {
    List<Tweet> findBySentiment(Sentiment sentiment);
}
