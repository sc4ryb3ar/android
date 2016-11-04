package com.bitlove.fetlife.event;

public class ServiceCallFinishedEvent {

    private String serviceCallAction;
    private int itemCount;

    public ServiceCallFinishedEvent(String serviceCallAction, int itemCount) {
        this.serviceCallAction = serviceCallAction;
        this.itemCount = itemCount;
    }

    public String getServiceCallAction() {
        return serviceCallAction;
    }

    public int getItemCount() {
        return itemCount;
    }
}
