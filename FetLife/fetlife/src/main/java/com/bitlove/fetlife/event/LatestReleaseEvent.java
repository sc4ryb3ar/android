package com.bitlove.fetlife.event;

import com.bitlove.fetlife.model.pojos.github.Release;

public class LatestReleaseEvent {

    private final Release latestRelease;

    public LatestReleaseEvent(Release latestRelease) {
        this.latestRelease = latestRelease;
    }

    public Release getLatestRelease() {
        return latestRelease;
    }
}
