package org.intuit.trendingdata;

import lombok.Data;

import java.util.List;

@Data
public class TweetsGroup {
    private List<TweetData> data;
    private TweetGroupMetaData meta;

    @Data
    public static class TweetData {
        private String id;
        private String text;
        //private List<String> edit_history_tweet_ids;

    }

    @Data
    public static class TweetGroupMetaData {
        private String newest_id;
        private String oldest_id;
        private int result_count;
        private String next_token;
    }

}
