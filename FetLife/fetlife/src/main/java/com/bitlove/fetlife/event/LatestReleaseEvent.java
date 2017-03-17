package com.bitlove.fetlife.event;

import com.bitlove.fetlife.model.pojos.github.Release;

public class LatestReleaseEvent {

    private final Release latestRelease;
    private final Release latestPreRelease;

    public LatestReleaseEvent(Release latestRelease, Release latestPreRelease) {
        this.latestRelease = latestRelease;
        this.latestPreRelease = latestPreRelease;
    }

    public Release getLatestRelease() {
        return latestRelease;
    }

    public Release getLatestPreRelease() {
        return latestPreRelease;
    }
}
