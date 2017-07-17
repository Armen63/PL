package com.example.armen.pl.io.bus.event;

import android.support.annotation.Nullable;

/**
 * Created by simonyan51 on 7/2/17.
 */


public class ApiEvent<T> {

    public static class EventType {
        public static final int PRODUCT_LIST_LOADED = 100;
        public static final int PRODUCT_ITEM_LOADED = 101;
    }

    private int eventType;
    private boolean success;
    private T eventData;

    public ApiEvent() {
    }

    public ApiEvent(int eventType, T eventData) {
        this.eventType = eventType;
        this.eventData = eventData;
    }

    public ApiEvent(int eventType, boolean success) {
        this.eventType = eventType;
        this.success = success;
    }

    public ApiEvent(int eventType, boolean success, T eventData) {
        this.eventType = eventType;
        this.success = success;
        this.eventData = eventData;
    }

    @Nullable
    public T getEventData() {
        return eventData;
    }

    public void setEventData(T eventData) {
        this.eventData = eventData;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}