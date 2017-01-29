package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VideoUploadIdResult {

    @JsonProperty("video_upload_id")
    private String videoUploadId;

    public String getVideoUploadId() {
        return videoUploadId;
    }

    public void setVideoUploadId(String videoUploadId) {
        this.videoUploadId = videoUploadId;
    }
}
