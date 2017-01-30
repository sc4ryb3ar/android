package com.bitlove.fetlife.event;

import com.bitlove.fetlife.model.service.FetLifeApiIntentService;

public class VideoChunkUploadFailedEvent extends ServiceCallFailedEvent {

    private final int chunk;
    private final int chunkCount;
    private String videoId;

    public VideoChunkUploadFailedEvent(String videoId, int chunk, int chunkCount) {
        super(FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE, false);
        this.videoId = videoId;
        this.chunk = chunk;
        this.chunkCount = chunkCount;
    }

    public String getVideoId() {
        return videoId;
    }

    public int getChunk() {
        return chunk;
    }

    public int getChunkCount() {
        return chunkCount;
    }
}
