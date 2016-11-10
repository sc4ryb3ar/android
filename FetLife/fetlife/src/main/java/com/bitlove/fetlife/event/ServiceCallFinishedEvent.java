package com.bitlove.fetlife.event;

public class ServiceCallFinishedEvent {

    private String[] params;
    private String serviceCallAction;
    private int itemCount;

    public ServiceCallFinishedEvent(String serviceCallAction, int itemCount) {
        this.serviceCallAction = serviceCallAction;
        this.itemCount = itemCount;
    }

    public ServiceCallFinishedEvent(String action, int count, String... params) {
        this.params = params;
    }

    public String getServiceCallAction() {
        return serviceCallAction;
    }

    public int getItemCount() {
        return itemCount;
    }

    public String[] getParams() {
        return params;
    }
}
