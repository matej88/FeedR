package se.chalmers.exjobb.feedr.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Collection;
import java.util.Map;

public class Session {

    private int sessionId;
    private boolean finished;

    @Exclude
    private String sessionKey;



    public Session() {
    }

    public Session(int sessionId, boolean finished) {
        this.sessionId = sessionId;
        this.finished = finished;

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

}
