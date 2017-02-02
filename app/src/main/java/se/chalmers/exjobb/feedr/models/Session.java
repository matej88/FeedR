package se.chalmers.exjobb.feedr.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * Created by matej on 2017-01-31.
 */

public class Session {

    private int sessionId;
    private boolean finished;
    private Map<String,String> timestamp;

    @JsonIgnore
    private String sessionKey;



    public Session() {
    }

    public Session(int sessionId, boolean finished, Map<String,String>  timestamp) {
        this.sessionId = sessionId;
        this.finished = finished;
        this.timestamp = timestamp;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }
}
