package com.bitlove.fetlife.model.inmemory;

import android.util.SparseArray;

import com.bitlove.fetlife.model.pojos.FeedStory;

import java.util.ArrayList;
import java.util.List;

/**
 * Store class for resource objects that should nto into the Database
 */
public class InMemoryStorage {

    private SparseArray<List<FeedStory>> feeds = new SparseArray<>();

    public void clearFeed() {
        synchronized (feeds) {
            feeds.clear();
        }
    }

    public void addFeed(int page, List<FeedStory> stories) {
        synchronized (feeds) {
            feeds.put(page,stories);
        }
    }

    public List<FeedStory> getFeed() {
        synchronized (feeds) {
            List<FeedStory> feed = new ArrayList<>();
            for (int i = 0; i < feeds.size(); i++) {
                feed.addAll(feeds.get(feeds.keyAt(i)));
            }
            return feed;
        }
    }


}
