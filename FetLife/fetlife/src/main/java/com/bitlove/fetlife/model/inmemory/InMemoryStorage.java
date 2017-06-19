package com.bitlove.fetlife.model.inmemory;

import android.util.SparseArray;

import com.bitlove.fetlife.model.pojos.fetlife.json.Event;
import com.bitlove.fetlife.model.pojos.fetlife.json.Story;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Store class for resource objects that should not go into the Database
 */
public class InMemoryStorage {

    private SparseArray<List<Story>> feeds = new SparseArray<>();
    private SparseArray<List<Story>> profileFeeds = new SparseArray<>();
    private Map<LatLng,Event> mapEvents = new HashMap<>();

    public void clearFeed() {
        synchronized (feeds) {
            feeds.clear();
        }
    }

    public void addFeed(int page, List<Story> stories) {
        synchronized (feeds) {
            feeds.put(page,stories);
        }
    }

    public List<Story> getFeed() {
        synchronized (feeds) {
            List<Story> feed = new ArrayList<>();
            for (int i = 0; i < feeds.size(); i++) {
                feed.addAll(feeds.get(feeds.keyAt(i)));
            }
            return feed;
        }
    }

    public void clearProfileFeed() {
        synchronized (profileFeeds) {
            profileFeeds.clear();
        }
    }

    public void addProfileFeed(int page, List<Story> stories) {
        synchronized (feeds) {
            profileFeeds.put(page,stories);
        }
    }

    public List<Story> getProfileFeed() {
        synchronized (feeds) {
            List<Story> feed = new ArrayList<>();
            for (int i = 0; i < profileFeeds.size(); i++) {
                feed.addAll(profileFeeds.get(profileFeeds.keyAt(i)));
            }
            return feed;
        }
    }

    public Map<LatLng,Event> getMapEvents() {
        return mapEvents;
    }
}
