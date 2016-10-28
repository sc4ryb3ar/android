
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Huge {

    @JsonProperty("width")
    private int width;
    @JsonProperty("url")
    private String url;
    @JsonProperty("height")
    private int height;

    /**
     * 
     * @return
     *     The width
     */
    @JsonProperty("width")
    public int getWidth() {
        return width;
    }

    /**
     * 
     * @param width
     *     The width
     */
    @JsonProperty("width")
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * 
     * @return
     *     The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return
     *     The height
     */
    @JsonProperty("height")
    public int getHeight() {
        return height;
    }

    /**
     * 
     * @param height
     *     The height
     */
    @JsonProperty("height")
    public void setHeight(int height) {
        this.height = height;
    }

}
