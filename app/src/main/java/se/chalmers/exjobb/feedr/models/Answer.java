package se.chalmers.exjobb.feedr.models;

/**
 * Created by matej on 2017-01-06.
 */

public class Answer {

    private String username;
    private String answer;

    public Answer() {
    }

    public Answer(String username, String answer) {
        this.username = username;
        this.answer = answer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
