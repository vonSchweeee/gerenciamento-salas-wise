package com.example.gerenciamentodesalas.model;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;

public class Event {
    private String eventName;
    private String eventMsg;
    @Nullable  private int eventStatusCode;
    public Event() {
    }

    public Event(String eventName, String eventMsg, @Nullable int eventStatusCode) {
        this.eventName = eventName;
        this.eventMsg = eventMsg;
        this.eventStatusCode = eventStatusCode;
    }

    public int getEventStatusCode() {
        return eventStatusCode;
    }

    public Event setEventStatusCode(int eventStatusCode) {
        this.eventStatusCode = eventStatusCode;
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventMsg() {
        return eventMsg;
    }

    public void setEventMsg(String eventMsg) {
        this.eventMsg = eventMsg;
    }
}